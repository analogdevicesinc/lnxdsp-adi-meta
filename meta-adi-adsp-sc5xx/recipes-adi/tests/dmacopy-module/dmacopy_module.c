#include <linux/init.h>
#include <linux/module.h>
#include <linux/of.h>
#include <linux/of_device.h>
#include <linux/genalloc.h>
#include <linux/slab.h>
#include <linux/string.h>
#include <linux/dma-mapping.h>
#include <mach/hardware.h>
#include <mach/cpu.h>
#include <mach/dma.h>
#include <mach/sram.h>
#include <linux/platform_device.h>

#define ARRAYSIZE(arr) (sizeof(arr) / sizeof(*arr))

static struct gen_pool *sram_pool;

static void * sram_alloc(size_t size);
static void sram_free(void * vaddr, size_t size);

static inline void show_diff(int first, int last)
{
	printk(KERN_ALERT " %i", first);
	if (first != last)
		printk(KERN_ALERT "...%i", last);
}
static void dump_diff(char *ptr_a, char *ptr_b, int len)
{
	int i, first = -1, last;
	printk(KERN_ALERT "\toffsets differ:");
	for (i = 0; i < len; ++i) {
		if (ptr_a[i] != ptr_b[i]) {
			if (first == -1)
				first = i;
			last = i;
		} else if (first != -1) {
			show_diff(first, last);
			first = last = -1;
		}
	}
	if (first != -1)
		show_diff(first, last);
	printk(KERN_ALERT "\n");
}

/* Make sure dma_memcpy() does not over/under flow the buffers given to it.
 * We do this by padding each buffer with a canary at the start and end.  So
 * the actual allocation looks like:
 * [size][canary][ ... buffer ... ][canary]
 * The canary used is the pointer returned by malloc so we know we have a
 * unique value at all times.  If dma_memcpy() does anything bad, the canaries
 * will get killed in the process.
 */
#define canary_size (int)sizeof(char *)
static void *xmalloc(size_t size, char *alloc_type)
{
	char *canary, *canary1, *canary2, *ret;

	if (!strncmp(alloc_type, "SDRAM", 5))
		ret = kmalloc(size + (canary_size * 2) + sizeof(size), GFP_KERNEL);
	else if (!strncmp(alloc_type, "SRAM", 4))
		ret = sram_alloc(size + (canary_size * 2) + sizeof(size));
	else {
		printk(KERN_ALERT "FAIL: wrong alloc_type %s for xmalloc\n", alloc_type);
		return NULL;
	}

	if (!ret) {
		printk(KERN_ALERT "FAIL: xmalloc(%zu) failed\n", size);
		return NULL;
	}
	canary = (void *)&ret;
	canary1 = ret + canary_size;
	canary2 = canary1 + canary_size + size;
	memcpy(ret, &size, sizeof(size));
	memcpy(canary1, canary, canary_size);
	memcpy(canary2, canary, canary_size);

	return ret + sizeof(size) + canary_size;
}

static void xfree(void *ptr, char *alloc_type)
{
	char *ret;
	char *canary, *canary1, *canary2;
	size_t size;
	ret = (char *)ptr - sizeof(size) - canary_size;
	canary = (void *)&ret;
	memcpy(&size, ret, sizeof(size));
	canary1 = ret + canary_size;
	canary2 = canary1 + canary_size + size;
	if (memcmp(canary, canary1, canary_size))
		printk(KERN_ALERT "FAIL: leading canary was killed {%i,%i,%i,%i} vs {%i,%i,%i,%i}!\n",
			canary1[0], canary1[1], canary1[2], canary1[3], canary[0], canary[1], canary[2], canary[3]);
	if (memcmp(canary, canary2, canary_size))
		printk(KERN_ALERT "FAIL: trailing canary was killed {%i,%i,%i,%i} vs {%i,%i,%i,%i}!\n",
			canary2[0], canary2[1], canary2[2], canary2[3], canary[0], canary[1], canary[2], canary[3]);

	if (!strncmp(alloc_type, "SDRAM", 5))
		kfree(ret);
	else if (!strncmp(alloc_type, "SRAM", 4))
		sram_free(ret, size);
	else {
		printk(KERN_ALERT "FAIL: wrong alloc_type %s for xfree\n", alloc_type);
		return;
	}
}


/* Do the actual test:
 *  - set buffers to values known to be different
 *  - copy src to chk
 *  - copy chk to dst
 *  - compare src and dst
 */
int _do_test(char *src_desc, char *dst_desc, char *chk_desc, char *src, char *dst, char *chk, int size, struct device * dev)
{
	static int test_num = 1;
	int ret = 0, i;
	dma_addr_t	ptr;
	dma_addr_t	src_handle, chk_handle, dst_handle;

	memset(src, 's', size);
	memset(dst, 'd', size);
	memset(chk, 'c', size);

	if (!strncmp(src_desc, "SDRAM", 5)) {
		src_handle = dma_map_single(dev, src, size, DMA_TO_DEVICE);
		if (dma_mapping_error(dev, src_handle)) {
			dev_err(dev, "Couldn't DMA map a 10 byte buffer\n");
			return -1;
		}
	} else if (!strncmp(src_desc, "SRAM", 4))
		src_handle = gen_pool_virt_to_phys(sram_pool, src);

	if (!strncmp(chk_desc, "SDRAM", 5)) {
		chk_handle = dma_map_single(dev, chk, size, DMA_FROM_DEVICE);
		if (dma_mapping_error(dev, chk_handle)) {
			dev_err(dev, "Couldn't DMA map a 10 byte buffer\n");
			if (!strncmp(src_desc, "SDRAM", 5))
				dma_unmap_single(dev, src_handle, size, DMA_TO_DEVICE);
			return -1;
		}
	} else if (!strncmp(chk_desc, "SRAM", 4))
		chk_handle = gen_pool_virt_to_phys(sram_pool, chk);

	ptr = dma_memcpy(chk_handle, src_handle, size);
	if (ptr)
		printk(KERN_ALERT "PASS: dma_memcpy %s[s] to %s[c]\n", src_desc, chk_desc);
	else
		printk(KERN_ALERT "FAIL: dma_memcpy %s[s] to %s[c]\n", src_desc, chk_desc), ++ret;

	if (!strncmp(src_desc, "SDRAM", 5))
		dma_unmap_single(dev, src_handle,
			 size, DMA_TO_DEVICE);

	if (!strncmp(chk_desc, "SDRAM", 5))
		dma_unmap_single(dev, chk_handle,
			size, DMA_FROM_DEVICE);

	i = memcmp(chk, src, size);
	if (!i)
		printk(KERN_ALERT "PASS: dma_memcpy(chk, src) test case %i, memcmp result is %d\n", test_num, i);
	else {
		printk(KERN_ALERT "FAIL: dma_memcpy(chk, src) test case %i, memcmp result is %d\n", test_num, i), ++ret;
		dump_diff(chk, src, size);
	}

	if (!strncmp(dst_desc, "SDRAM", 5)) {
		dst_handle = dma_map_single(dev, dst, size, DMA_FROM_DEVICE);
		if (dma_mapping_error(dev, dst_handle)) {
			dev_err(dev, "Couldn't DMA map a 10 byte buffer\n");
			return -1;
		}
	} else if (!strncmp(dst_desc, "SRAM", 4))
		dst_handle = gen_pool_virt_to_phys(sram_pool, dst);

	if (!strncmp(chk_desc, "SDRAM", 5)) {
		chk_handle = dma_map_single(dev, chk, size, DMA_FROM_DEVICE);
		if (dma_mapping_error(dev, chk_handle)) {
			dev_err(dev, "Couldn't DMA map a 10 byte buffer\n");
			if (!strncmp(dst_desc, "SDRAM", 5))
				dma_unmap_single(dev, dst_handle, size, DMA_FROM_DEVICE);
			return -1;
		}
	} else if (!strncmp(chk_desc, "SRAM", 4))
		chk_handle = gen_pool_virt_to_phys(sram_pool, chk);


	ptr = dma_memcpy(dst_handle, chk_handle, size);
	if (ptr)
		printk(KERN_ALERT "PASS: dma_memcpy %s[c] to %s[d]\n", chk_desc, dst_desc);
	else
		printk(KERN_ALERT "FAIL: dma_memcpy %s[c] to %s[d]\n", chk_desc, dst_desc), ++ret;

	if (!strncmp(dst_desc, "SDRAM", 5))
		dma_unmap_single(dev, dst_handle,
			 size, DMA_FROM_DEVICE);

	if (!strncmp(chk_desc, "SDRAM", 5))
		dma_unmap_single(dev, chk_handle,
			size, DMA_FROM_DEVICE);

	i = memcmp(dst, chk, size);
	if (!i)
		printk(KERN_ALERT "PASS: dma_memcpy(dst, chk) test case %i, memcmp result is %d\n", test_num, i);
	else {
		printk(KERN_ALERT "FAIL: dma_memcpy(dst, chk) test case %i, memcmp result is %d\n", test_num, i), ++ret;
		dump_diff(dst, chk, size);
	}

	i = memcmp(dst, src, size);
	if (!i)
		printk(KERN_ALERT "PASS: dma_memcpy(dst, src) test case %i, memcmp result is %d\n", test_num, i);
	else {
		printk(KERN_ALERT "FAIL: dma_memcpy(dst, src) test case %i, memcmp result is %d\n", test_num, i), ++ret;
		dump_diff(dst, src, size);
	}

	++test_num;

	return ret;
}

/*
 * test case 1 - dma_memcpy from src(SDRAM) to sram(SRAM) and sram(SRAM) to dst(SDRAM), then compare
 *               src(SDRAM) and dst(SDRAM) to make sure that copy into or from SRAM is ok.
 *               also check that 8/16/32 bit transfers work by mucking with alignment.
 */
int sram_test(int size, struct device * dev)
{
	int ret = 0;
	char *src = xmalloc(size, "SDRAM");
	char *dst = xmalloc(size, "SDRAM");
	char *sram = sram_alloc(size);

	printk(KERN_ALERT "TEST:  --- SRAM (L2) <-> SDRAM w/%i bytes ---\n", size);

	if (!sram) {
		printk(KERN_ALERT "FAIL: sram_alloc(%i) failed\n", size);
		return 1;
	}

	if ((ulong)src % 4 != 0 || (ulong)dst % 4 != 0 ||
		(ulong)sram % 4 != 0 || size % 4 != 0) {
		printk(KERN_ALERT "FAIL: SRAM src/dst/size are not 32bit aligned to start:\n"
			"\t%p / %p / %p / %i\n",
			src, dst, sram, size);
		return 1;
	}

	ret += _do_test("SDRAMx32", "SDRAMx32", "SRAM", src, dst, sram, size, dev);
	ret += _do_test("SDRAMx16", "SDRAMx16", "SRAM", src+2, dst+2, sram+2, size-2, dev);
	ret += _do_test("SDRAMx8", "SDRAMx8", "SRAM", src+1, dst+1, sram+1, size-1, dev);

	xfree(src, "SDRAM");
	xfree(dst, "SDRAM");
	sram_free(sram, size);

	return ret;
}

/*
 * test case 2 - dma_memcpy from src(SDRAM) to dst(SDRAM), memcmp of src and dst,
 *               make sure that dma_memcpy in SDRAM is ok
 *               also check that 8/16/32 bit transfers work by mucking with alignment.
 */
int sdram_test(int size, struct device * dev)
{
	int ret = 0;
	char *src = xmalloc(size, "SDRAM");
	char *dst = xmalloc(size, "SDRAM");
	char *chk = xmalloc(size, "SDRAM");

	printk(KERN_ALERT "TEST:  --- SDRAM <-> SDRAM w/%i bytes ---\n", size);

	if ((ulong)src % 4 != 0 || (ulong)dst % 4 != 0 ||
		(ulong)chk % 4 != 0 || size % 4 != 0) {
		printk(KERN_ALERT "FAIL: SDRAM src/dst/size are not 32bit aligned to start\n"
			"\t%p / %p / %p / %i\n",
			src, dst, chk, size);
		return 1;
	}

	ret += _do_test("SDRAMx32", "SDRAMx32", "SDRAM", src, dst, chk, size, dev);
	ret += _do_test("SDRAMx16", "SDRAMx16", "SDRAM", src+2, dst+2, chk+2, size-2, dev);
	ret += _do_test("SDRAMx8", "SDRAMx8", "SDRAM", src+1, dst+1, chk+1, size-1, dev);

	xfree(src, "SDRAM");
	xfree(dst, "SDRAM");
	xfree(chk, "SDRAM");

	return ret;
}

/*
 * test case 3 - dma_memcpy from src(SRAM) to dst(SRAM), memcmp of src and dst,
 *               make sure that dma_memcpy in SRAM is ok
 *               also check that 8/16/32 bit transfers work by mucking with alignment.
 */
int insram_test(int size, struct device * dev)
{
	int ret = 0;
	char *src = xmalloc(size, "SRAM");
	char *dst = xmalloc(size, "SRAM");
	char *sram = sram_alloc(size);

	printk(KERN_ALERT "TEST:  --- SRAM (L2) <-> SRAM (L2) w/%i bytes ---\n", size);

	if (!sram) {
		printk(KERN_ALERT "FAIL: sram_alloc(%i) failed\n", size);
		return 1;
	}

	if ((ulong)src % 4 != 0 || (ulong)dst % 4 != 0 ||
		(ulong)sram % 4 != 0 || size % 4 != 0) {
		printk(KERN_ALERT "FAIL: SRAM src/dst/size are not 32bit aligned to start:\n"
			"\t%p / %p / %p / %i\n",
			src, dst, sram, size);
		return 1;
	}

	ret += _do_test("SRAMx32", "SRAMx32", "SRAM", src, dst, sram, size, dev);
	ret += _do_test("SRAMx16", "SRAMx16", "SRAM", src+2, dst+2, sram+2, size-2, dev);
	ret += _do_test("SRAMx8", "SRAMx8", "SRAM", src+1, dst+1, sram+1, size-1, dev);

	xfree(src, "SRAM");
	xfree(dst, "SRAM");
	sram_free(sram, size);

	return ret;
}

static const struct of_device_id adi_dmacopy_of_match[] = {
	{ .compatible = "adi,dmacopy" },
	{ },
};

static void * sram_alloc(size_t size)
{
	unsigned long vaddr;
	vaddr = gen_pool_alloc(sram_pool, size);
	printk(KERN_INFO "alloc address: 0x%x, size: 0x%x\n", vaddr, size);
	return (void *)vaddr;
}

static void sram_free(void * vaddr, size_t size)
{
	gen_pool_free(sram_pool, (unsigned long)vaddr, size);
	printk(KERN_INFO "free address: 0x%x, size: 0x%x\n", vaddr, size);
}

#define TEST_RANGE(range, func, args...) \
	for (i = 0; i < ARRAYSIZE(range##_range); ++i) \
		ret += func(range##_range[i], ## args)

MODULE_LICENSE("Dual BSD/GPL");

static int dmacopy_test_init(struct device * dev)
{
	int ret = 0, i;
	int sml_range[] = { 4, 0x10, 0x1000 };
	int mid_range[] = { 4, 0x10, 0x1000, 0x10000 };

	printk(KERN_ALERT "------Start testing dma_memcpy------\n");

	TEST_RANGE(sml, sram_test, dev);
	TEST_RANGE(sml, sdram_test, dev);
	TEST_RANGE(sml, insram_test, dev);

	TEST_RANGE(mid, sram_test, dev);
	TEST_RANGE(mid, sdram_test, dev);
	TEST_RANGE(mid, insram_test, dev);

    if (ret)
		printk(KERN_ALERT "SUMMARY: %i tests failed\n", ret);
	else
		printk(KERN_ALERT "SUMMARY: all tests passed\n");

	return 0;
}

static int adi_dmacopy_probe(struct platform_device *pdev)
{
	int i;
	const struct of_device_id *match;
	struct device *dev;

	dev = &pdev->dev;

	match = of_match_device(of_match_ptr(adi_dmacopy_of_match), &pdev->dev);
	if(!match) {
		pr_err("No dmacopy_module_of_match device defined in dts file\n");
		return -ENODEV;
	}

	sram_pool = of_gen_pool_get(dev->of_node, "adi,sram", 0);
	if (!sram_pool) {
		pr_err("Unable to get sram pool!\n");
		return -ENODEV;
	}

	dmacopy_test_init(dev);

	return 0;
}

static int adi_dmacopy:remove(struct platform_device *pdev)
{
	printk(KERN_INFO "Goodbye, cruel world\n");
	return 0;
}

static struct platform_driver adi_dmacopy_driver = {
	.probe = adi_dmacopy_probe,
	.remove = adi_dmacopy:remove,
	.driver = {
		.name = "adi_dmacopy",
		.of_match_table = of_match_ptr(adi_dmacopy_of_match),
	},
};

module_platform_driver(adi_dmacopy_driver);
MODULE_DESCRIPTION("ADI DMACOPY Test Driver");
