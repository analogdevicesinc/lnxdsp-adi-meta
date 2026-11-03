// SPDX-License-Identifier: GPL-2.0-only
/*
 * Copyright 2026 Analog Devices Inc.
 *
 * DMA copy test module
 *
 * Copies buffers between DDR and on-chip SRAM with a memory DMA (MDMA)
 * channel, through the dmaengine API, and checks the copied data and the
 * canaries around each buffer. Each copy is run 32-bit, 16-bit and 8-bit
 * aligned. Loading the module runs the tests, and the load fails if any
 * test fails.
 */
#include <linux/completion.h>
#include <linux/dma-mapping.h>
#include <linux/dmaengine.h>
#include <linux/genalloc.h>
#include <linux/io.h>
#include <linux/module.h>
#include <linux/of.h>
#include <linux/of_platform.h>
#include <linux/of_reserved_mem.h>
#include <linux/platform_device.h>
#include <linux/slab.h>

#define CANARY_SIZE	16
#define CANARY		0xa5

enum mem_type { DDR, SRAM };

static const char * const mem_name[] = { "SDRAM", "SRAM" };

struct test_buf {
	enum mem_type type;
	u8 *alloc;		/* allocation, including the canaries */
	size_t alloc_len;
	phys_addr_t alloc_phys;	/* SRAM only */
	u8 *cpu;		/* the part of the allocation that is copied */
	size_t len;
	dma_addr_t dma;
	enum dma_data_direction dir;
};

static struct dma_chan *chan;
static struct device *dma_dev;
static bool shared_chan;

/*
 * On ARMv7 parts the SRAM is a gen_pool of ioremapped memory. On ARMv8 parts
 * it is a reserved-memory region in the linear map, used here as a stack. The
 * region is only used when the sram_mmap driver is bound to it, as the device
 * tree can describe SRAM that the kernel does not set up for the Arm cores:
 * on the ADSP-SC846, writing to it raises an SError.
 */
static struct gen_pool *sram_pool;
static u8 *sram_base;
static phys_addr_t sram_phys;
static size_t sram_size;
static size_t sram_used;

static void *sram_alloc(size_t len, phys_addr_t *phys)
{
	unsigned long vaddr;
	u8 *ptr;

	if (sram_pool) {
		vaddr = gen_pool_alloc(sram_pool, len);
		if (!vaddr)
			return NULL;
		*phys = gen_pool_virt_to_phys(sram_pool, vaddr);
		return (void *)vaddr;
	}

	len = ALIGN(len, L1_CACHE_BYTES);
	if (!sram_base || sram_used + len > sram_size)
		return NULL;

	ptr = sram_base + sram_used;
	*phys = sram_phys + sram_used;
	sram_used += len;
	return ptr;
}

/* Region buffers must be freed in the reverse order of allocation */
static void sram_free(void *ptr, size_t len)
{
	if (sram_pool)
		gen_pool_free(sram_pool, (unsigned long)ptr, len);
	else
		sram_used -= ALIGN(len, L1_CACHE_BYTES);
}

/* True if the sram_mmap driver is bound to the device tree node */
static bool sram_mmap_bound(struct device_node *np)
{
	struct platform_device *pdev = of_find_device_by_node(np);
	bool bound;

	if (!pdev)
		return false;

	bound = device_is_bound(&pdev->dev);
	put_device(&pdev->dev);
	return bound;
}

static int sram_init(void)
{
	struct device_node *np, *mem_np;
	struct reserved_mem *rmem;
	int i;

	for_each_compatible_node(np, NULL, "adi,sram-mmap") {
		mem_np = of_parse_phandle(np, "memory-region", 0);
		if (!mem_np)
			continue;

		if (!sram_mmap_bound(np)) {
			pr_info("Not using the SRAM in %pOF, as the sram_mmap driver is not bound to it\n",
				np);
			of_node_put(mem_np);
			continue;
		}

		rmem = of_reserved_mem_lookup(mem_np);
		of_node_put(mem_np);
		if (rmem && rmem->size) {
			sram_base = memremap(rmem->base, rmem->size, MEMREMAP_WB);
			if (sram_base) {
				sram_phys = rmem->base;
				sram_size = rmem->size;
				of_node_put(np);
				return 0;
			}
		}
	}

	for (i = 0; i < 2; i++) {
		np = of_find_compatible_node(NULL, NULL,
			i ? "adi,sram-controller" : "adi,sram-mmap");
		if (!np)
			continue;
		sram_pool = of_gen_pool_get(np, "adi,sram",
			of_count_phandle_with_args(np, "adi,sram", NULL) - 1);
		of_node_put(np);
		if (sram_pool)
			return 0;
	}

	return -ENODEV;
}

static void sram_exit(void)
{
	if (sram_base)
		memunmap(sram_base);
}

static int buf_alloc(struct test_buf *buf, enum mem_type type, size_t len,
		     int offset)
{
	buf->type = type;
	buf->len = len;
	buf->alloc_len = len + offset + 2 * CANARY_SIZE;

	if (type == DDR)
		buf->alloc = kmalloc(buf->alloc_len, GFP_KERNEL);
	else
		buf->alloc = sram_alloc(buf->alloc_len, &buf->alloc_phys);

	if (!buf->alloc) {
		pr_err("FAIL: unable to allocate %zu bytes of %s\n",
			buf->alloc_len, mem_name[type]);
		return -ENOMEM;
	}

	buf->cpu = buf->alloc + CANARY_SIZE + offset;
	memset(buf->alloc, CANARY, buf->alloc_len);
	return 0;
}

static void buf_free(struct test_buf *buf)
{
	if (!buf->alloc)
		return;

	if (buf->type == DDR)
		kfree(buf->alloc);
	else
		sram_free(buf->alloc, buf->alloc_len);
	buf->alloc = NULL;
}

static int buf_map(struct test_buf *buf, enum dma_data_direction dir)
{
	buf->dir = dir;

	if (buf->type == SRAM && sram_pool)
		buf->dma = dma_map_resource(dma_dev,
			buf->alloc_phys + (buf->cpu - buf->alloc), buf->len, dir, 0);
	else
		buf->dma = dma_map_single(dma_dev, buf->cpu, buf->len, dir);

	if (dma_mapping_error(dma_dev, buf->dma)) {
		pr_err("FAIL: unable to DMA map %zu bytes of %s\n",
			buf->len, mem_name[buf->type]);
		return -ENOMEM;
	}

	return 0;
}

static void buf_unmap(struct test_buf *buf)
{
	if (buf->type == SRAM && sram_pool)
		dma_unmap_resource(dma_dev, buf->dma, buf->len, buf->dir, 0);
	else
		dma_unmap_single(dma_dev, buf->dma, buf->len, buf->dir);
}

static int check_canaries(struct test_buf *buf)
{
	u8 *tail = buf->cpu + buf->len;
	u8 *p;

	for (p = buf->alloc; p < buf->cpu; p++)
		if (*p != CANARY)
			goto fail;

	for (p = tail; p < buf->alloc + buf->alloc_len; p++)
		if (*p != CANARY)
			goto fail;

	return 0;

fail:
	pr_err("FAIL: %s canary at offset %td was overwritten with 0x%02x\n",
		p < buf->cpu ? "leading" : "trailing", p - buf->cpu, *p);
	return 1;
}

static void dma_done(void *arg)
{
	complete(arg);
}

static int dma_copy(struct test_buf *dst, struct test_buf *src)
{
	struct dma_async_tx_descriptor *tx;
	DECLARE_COMPLETION_ONSTACK(done);
	dma_cookie_t cookie;
	int ret;

	ret = buf_map(src, DMA_TO_DEVICE);
	if (ret)
		return ret;

	ret = buf_map(dst, DMA_FROM_DEVICE);
	if (ret)
		goto unmap_src;

	tx = dmaengine_prep_dma_memcpy(chan, dst->dma, src->dma, src->len,
				       DMA_PREP_INTERRUPT | DMA_CTRL_ACK);
	if (!tx) {
		pr_err("FAIL: unable to prepare the DMA copy\n");
		ret = -EIO;
		goto unmap_dst;
	}

	tx->callback = dma_done;
	tx->callback_param = &done;
	cookie = dmaengine_submit(tx);
	if (dma_submit_error(cookie)) {
		pr_err("FAIL: unable to submit the DMA copy\n");
		ret = -EIO;
		goto unmap_dst;
	}

	dma_async_issue_pending(chan);

	if (!wait_for_completion_timeout(&done, msecs_to_jiffies(1000))) {
		pr_err("FAIL: DMA copy timed out\n");
		dmaengine_terminate_sync(chan);
		ret = -ETIMEDOUT;
	} else if (dma_async_is_tx_complete(chan, cookie, NULL, NULL) != DMA_COMPLETE) {
		pr_err("FAIL: DMA copy did not complete\n");
		ret = -EIO;
	}

unmap_dst:
	buf_unmap(dst);
unmap_src:
	buf_unmap(src);
	return ret;
}

/*
 * Copy src to chk, then chk to dst, and check that chk and dst match src and
 * that no canary was overwritten
 */
static int do_test(enum mem_type src_type, enum mem_type chk_type,
		   enum mem_type dst_type, size_t size, int offset)
{
	static int test_num = 1;
	struct test_buf src = {}, chk = {}, dst = {};
	size_t len = size - offset;
	int errors = 0;
	size_t i;

	if (buf_alloc(&src, src_type, len, offset) ||
	    buf_alloc(&chk, chk_type, len, offset) ||
	    buf_alloc(&dst, dst_type, len, offset)) {
		errors++;
		goto out;
	}

	for (i = 0; i < len; i++)
		src.cpu[i] = i * 7 + 1;
	memset(chk.cpu, 'c', len);
	memset(dst.cpu, 'd', len);

	if (dma_copy(&chk, &src)) {
		errors++;
		goto out;
	}

	if (memcmp(chk.cpu, src.cpu, len)) {
		pr_err("FAIL: %s[c] does not match %s[s]\n",
			mem_name[chk_type], mem_name[src_type]);
		errors++;
	}

	if (dma_copy(&dst, &chk)) {
		errors++;
		goto out;
	}

	if (memcmp(dst.cpu, src.cpu, len)) {
		pr_err("FAIL: %s[d] does not match %s[s]\n",
			mem_name[dst_type], mem_name[src_type]);
		errors++;
	}

	errors += check_canaries(&src) + check_canaries(&chk) +
		  check_canaries(&dst);

out:
	pr_info("%s: test %d: %s[s] -> %s[c] -> %s[d], %zu bytes, %d-bit aligned\n",
		errors ? "FAIL" : "PASS", test_num, mem_name[src_type],
		mem_name[chk_type], mem_name[dst_type], len,
		offset == 0 ? 32 : offset == 2 ? 16 : 8);
	test_num++;
	buf_free(&dst);
	buf_free(&chk);
	buf_free(&src);
	return errors ? 1 : 0;
}

/* Run a copy 32-bit, 16-bit and 8-bit aligned */
static int test_aligned(enum mem_type src, enum mem_type chk,
			enum mem_type dst, size_t size)
{
	return do_test(src, chk, dst, size, 0) +
	       do_test(src, chk, dst, size, 2) +
	       do_test(src, chk, dst, size, 1);
}

static int __init dmacopy_test_init(void)
{
	static const size_t sizes[] = { 4, 0x10, 0x1000, 0x10000 };
	size_t sram_limit;
	dma_cap_mask_t mask;
	int failed = 0;
	int i;

	/*
	 * adi_remoteproc holds a dmaengine reference and uses the public memcpy
	 * channel, so it cannot be requested for exclusive use. Share it in the
	 * same way, and only request a private channel if it is not public.
	 */
	dmaengine_get();
	chan = dma_find_channel(DMA_MEMCPY);
	if (chan) {
		shared_chan = true;
	} else {
		dmaengine_put();
		dma_cap_zero(mask);
		dma_cap_set(DMA_MEMCPY, mask);
		chan = dma_request_chan_by_mask(&mask);
		if (IS_ERR(chan)) {
			pr_err("FAIL: no memcpy DMA channel: %ld\n", PTR_ERR(chan));
			return PTR_ERR(chan);
		}
	}
	dma_dev = chan->device->dev;

	pr_info("------Start testing DMA memcpy on %s------\n", dma_chan_name(chan));

	if (sram_init())
		pr_info("No SRAM pool or region found, only testing SDRAM\n");

	/* The SRAM-only test needs three SRAM buffers */
	sram_limit = (sram_pool ? gen_pool_avail(sram_pool) : sram_size) / 3;
	if (sram_limit > 2 * CANARY_SIZE + L1_CACHE_BYTES)
		sram_limit -= 2 * CANARY_SIZE + L1_CACHE_BYTES;
	else
		sram_limit = 0;

	for (i = 0; i < ARRAY_SIZE(sizes); i++) {
		failed += test_aligned(DDR, DDR, DDR, sizes[i]);
		if (sram_pool || sram_base) {
			failed += test_aligned(DDR, SRAM, DDR, sizes[i]);
			if (sizes[i] <= sram_limit)
				failed += test_aligned(SRAM, SRAM, SRAM, sizes[i]);
		}
	}

	sram_exit();
	if (shared_chan)
		dmaengine_put();
	else
		dma_release_channel(chan);

	if (failed) {
		pr_err("SUMMARY: %d tests failed\n", failed);
		return -EIO;
	}

	pr_info("SUMMARY: all tests passed\n");
	return 0;
}

static void __exit dmacopy_test_exit(void)
{
}

module_init(dmacopy_test_init);
module_exit(dmacopy_test_exit);

MODULE_LICENSE("GPL");
MODULE_DESCRIPTION("ADI DMA copy test");
