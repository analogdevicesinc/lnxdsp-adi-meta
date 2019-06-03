#include <linux/init.h>
#include <linux/module.h>
#include <linux/slab.h>
#include <linux/string.h>
#include <mach/sram.h>
#include <mach/dma.h>

#define ARRAYSIZE(arr) (sizeof(arr) / sizeof(*arr))

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

/* Make sure dma_memcpy_nocache() does not over/under flow the buffers given to it.
 * We do this by padding each buffer with a canary at the start and end.  So
 * the actual allocation looks like:
 * [size][canary][ ... buffer ... ][canary]
 * The canary used is the pointer returned by malloc so we know we have a
 * unique value at all times.  If dma_memcpy_nocache() does anything bad, the canaries
 * will get killed in the process.
 */
#define canary_size (int)sizeof(char *)
static void *xmalloc(size_t size, char *alloc_type)
{
	char *canary, *canary1, *canary2, *ret;

	if (!strncmp(alloc_type, "SDRAM", 5)) {
		ret = kmalloc(size + (canary_size * 2) + sizeof(size), GFP_KERNEL);
	} else if (!strncmp(alloc_type, "SRAM", 4)) {
		ret = sram_alloc(size + (canary_size * 2) + sizeof(size));
	} else {
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

	if (!strncmp(alloc_type, "SDRAM", 5)) {
		kfree(ret);
	} else if (!strncmp(alloc_type, "SRAM", 4)) {
		sram_free(ret);
	} else {
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
int _do_test(char *src_desc, char *dst_desc, char *src, char *dst, char *chk, int size)
{
	static int test_num = 1;
	int ret = 0, i;
	void *ptr;

	memset(src, 's', size);
	memset(dst, 'd', size);

	memset(chk, 'c', size);

	ptr = dma_memcpy_nocache(chk, src, size);
	if (ptr)
		printk(KERN_ALERT "PASS: dma_memcpy_nocache %s[s] to %s[c]\n", src_desc, dst_desc);
	else
		printk(KERN_ALERT "FAIL: dma_memcpy_nocache %s[s] to %s[c]\n", src_desc, dst_desc), ++ret;

	i = memcmp(chk, src, size);
	if (!i)
		printk(KERN_ALERT "PASS: dma_memcpy_nocache(chk, src) test case %i, memcmp result is %d\n", test_num, i);
	else {
		printk(KERN_ALERT "FAIL: dma_memcpy_nocache(chk, src) test case %i, memcmp result is %d\n", test_num, i), ++ret;
		dump_diff(chk, src, size);
	}

	ptr = dma_memcpy_nocache(dst, chk, size);
	if (ptr)
		printk(KERN_ALERT "PASS: dma_memcpy_nocache %s[c] to %s[d]\n", dst_desc, src_desc);
	else
		printk(KERN_ALERT "FAIL: dma_memcpy_nocache %s[c] to %s[d]\n", dst_desc, src_desc), ++ret;

	i = memcmp(dst, chk, size);
	if (!i)
		printk(KERN_ALERT "PASS: dma_memcpy_nocache(dst, chk) test case %i, memcmp result is %d\n", test_num, i);
	else {
		printk(KERN_ALERT "FAIL: dma_memcpy_nocache(dst, chk) test case %i, memcmp result is %d\n", test_num, i), ++ret;
		dump_diff(dst, chk, size);
	}

	i = memcmp(dst, src, size);
	if (!i)
		printk(KERN_ALERT "PASS: dma_memcpy_nocache(dst, src) test case %i, memcmp result is %d\n", test_num, i);
	else {
		printk(KERN_ALERT "FAIL: dma_memcpy_nocache(dst, src) test case %i, memcmp result is %d\n", test_num, i), ++ret;
		dump_diff(dst, src, size);
	}

	++test_num;

	return ret;
}

/*
 * test case 1 - dma_memcpy_nocache from src(SDRAM) to sram(SRAM) and sram(SRAM) to dst(SDRAM), then compare
 *               src(SDRAM) and dst(SDRAM) to make sure that copy into or from SRAM is ok.
 *               also check that 8/16/32 bit transfers work by mucking with alignment.
 */
int sram_test(int size)
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
	    (ulong)sram % 4 != 0 || size % 4 != 0)
	{
		printk(KERN_ALERT "FAIL: SRAM src/dst/size are not 32bit aligned to start:\n"
			"\t%p / %p / %p / %i\n",
			src, dst, sram, size);
		return 1;
	}

	ret += _do_test("SDRAMx32", "SRAMx32", src, dst, sram, size);
	ret += _do_test("SDRAMx16", "SRAMx16", src+2, dst+2, sram+2, size-2);
	ret += _do_test("SDRAMx8", "SRAMx8", src+1, dst+1, sram+1, size-1);

	xfree(src, "SDRAM");
	xfree(dst, "SDRAM");
	sram_free(sram);

	return ret;
}

/*
 * test case 2 - dma_memcpy_nocache from src(SDRAM) to dst(SDRAM), memcmp of src and dst,
 *               make sure that dma_memcpy_nocache in SDRAM is ok
 *               also check that 8/16/32 bit transfers work by mucking with alignment.
 */
int sdram_test(int size)
{
	int ret = 0;
	char *src = xmalloc(size, "SDRAM");
	char *dst = xmalloc(size, "SDRAM");
	char *chk = xmalloc(size, "SDRAM");

	printk(KERN_ALERT "TEST:  --- SDRAM <-> SDRAM w/%i bytes ---\n", size);

	if ((ulong)src % 4 != 0 || (ulong)dst % 4 != 0 ||
	    (ulong)chk % 4 != 0 || size % 4 != 0)
	{
		printk(KERN_ALERT "FAIL: SDRAM src/dst/size are not 32bit aligned to start\n"
			"\t%p / %p / %p / %i\n",
			src, dst, chk, size);
		return 1;
	}

	ret += _do_test("SDRAMx32", "SDRAMx32", src, dst, chk, size);
	ret += _do_test("SDRAMx16", "SDRAMx16", src+2, dst+2, chk+2, size-2);
	ret += _do_test("SDRAMx8", "SDRAMx8", src+1, dst+1, chk+1, size-1);

	xfree(src, "SDRAM");
	xfree(dst, "SDRAM");
	xfree(chk, "SDRAM");

	return ret;
}

/*
 * test case 3 - dma_memcpy_nocache from src(SRAM) to dst(SRAM), memcmp of src and dst,
 *               make sure that dma_memcpy_nocache in SRAM is ok
 *               also check that 8/16/32 bit transfers work by mucking with alignment.
 */
int insram_test(int size)
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
	    (ulong)sram % 4 != 0 || size % 4 != 0)
	{
		printk(KERN_ALERT "FAIL: SRAM src/dst/size are not 32bit aligned to start:\n"
			"\t%p / %p / %p / %i\n",
			src, dst, sram, size);
		return 1;
	}

	ret += _do_test("SRAMx32", "SRAMx32", src, dst, sram, size);
	ret += _do_test("SRAMx16", "SRAMx16", src+2, dst+2, sram+2, size-2);
	ret += _do_test("SRAMx8", "SRAMx8", src+1, dst+1, sram+1, size-1);

	xfree(src, "SRAM");
	xfree(dst, "SRAM");
	sram_free(sram);

	return ret;
}

#define TEST_RANGE(range, func, args...) \
	for (i = 0; i < ARRAYSIZE(range##_range); ++i) \
		ret += func(range##_range[i], ## args)

MODULE_LICENSE("Dual BSD/GPL");

static int dmacopy_init(void)
{

    int ret = 0, i;
    int sml_range[] = { 4, 0x10, 0x1000 };
    int mid_range[] = { 4, 0x10, 0x1000, 0x10000, 0x12340 };

    printk(KERN_ALERT "------Start testing dma_memcpy_nocache------\n");

    TEST_RANGE(sml, sram_test);
    TEST_RANGE(sml, sdram_test);
    TEST_RANGE(sml, insram_test);

    TEST_RANGE(mid, sram_test);
    TEST_RANGE(mid, sdram_test);
    TEST_RANGE(mid, insram_test);

    if (ret)
	    printk(KERN_ALERT "SUMMARY: %i tests failed\n", ret);
    else
	    printk(KERN_ALERT "SUMMARY: all tests passed\n");

    return -1;
}

static void dmacopy_exit(void)
{
    printk(KERN_ALERT "Goodbye, cruel world\n");
}

module_init(dmacopy_init);
module_exit(dmacopy_exit);
