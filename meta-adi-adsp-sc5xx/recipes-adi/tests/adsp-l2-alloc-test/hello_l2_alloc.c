// SPDX-License-Identifier: GPL-2.0-only
/*
 * Copyright 2019-2026 Analog Devices Inc.
 *
 * L2 SRAM allocation test module
 *
 * Allocates blocks from each on-chip SRAM pool created by the kernel's
 * mmio-sram driver, and checks that each block is inside the SRAM, aligned
 * and does not overlap the others, that data written to the blocks reads
 * back, and that the pool returns to its original size when they are freed.
 * Loading the module runs the test, and the load fails if the test fails.
 *
 * The ADSP-SC598 and ADSP-SC846 device trees have no mmio-sram pools (their
 * SRAM is reserved memory used by sram_mmap), so the test is skipped there.
 */
#include <linux/genalloc.h>
#include <linux/io.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/of.h>
#include <linux/of_address.h>
#include <linux/of_platform.h>
#include <linux/platform_device.h>
#include <linux/sizes.h>

/* Block sizes from the original test, plus one large block */
static const size_t block_sizes[] = {
	32, 2 * 32, 4 * 32, 6 * 32, 8 * 32, 10 * 32, 12 * 32, 14 * 32, 16 * 32, 18 * 32,
};

#define NUM_BLOCKS	(ARRAY_SIZE(block_sizes) + 1)
#define LARGE_BLOCK_MAX	SZ_64K

struct block {
	unsigned long vaddr;
	size_t size;
	phys_addr_t phys;
};

static u32 pattern(int block, size_t word, u32 salt)
{
	return (0xa5000000 | (block << 16) | (word & 0xffff)) ^ salt;
}

static void fill(struct block *blocks, int n, u32 salt)
{
	size_t w;
	int i;

	for (i = 0; i < n; i++)
		for (w = 0; w < blocks[i].size / 4; w++)
			writel(pattern(i, w, salt),
			       (void __iomem *)(blocks[i].vaddr + w * 4));
}

static int verify(struct block *blocks, int n, u32 salt)
{
	int errors = 0;
	size_t w;
	u32 val;
	int i;

	for (i = 0; i < n; i++) {
		for (w = 0; w < blocks[i].size / 4; w++) {
			val = readl((void __iomem *)(blocks[i].vaddr + w * 4));
			if (val == pattern(i, w, salt))
				continue;
			if (errors < 8)
				pr_err("FAIL: block %d at %pa + 0x%zx: read 0x%08x, expected 0x%08x\n",
					i, &blocks[i].phys, w * 4, val, pattern(i, w, salt));
			errors++;
		}
	}

	return errors;
}

static int check_block(struct gen_pool *pool, const struct resource *res,
		       struct block *blocks, int i)
{
	size_t align = 1UL << pool->min_alloc_order;
	struct block *b = &blocks[i];
	int j;

	if (!gen_pool_has_addr(pool, b->vaddr, b->size)) {
		pr_err("FAIL: block %d is not in the pool\n", i);
		return 1;
	}

	if (b->vaddr & (align - 1)) {
		pr_err("FAIL: block %d is not aligned to %zu bytes\n", i, align);
		return 1;
	}

	if (b->phys == (phys_addr_t)-1 || b->phys < res->start ||
	    b->phys + b->size - 1 > res->end) {
		pr_err("FAIL: block %d at %pa is outside the SRAM %pR\n", i, &b->phys, res);
		return 1;
	}

	for (j = 0; j < i; j++) {
		if (b->vaddr < blocks[j].vaddr + blocks[j].size &&
		    blocks[j].vaddr < b->vaddr + b->size) {
			pr_err("FAIL: block %d overlaps block %d\n", i, j);
			return 1;
		}
	}

	return 0;
}

static int test_pool(struct device_node *np)
{
	struct block blocks[NUM_BLOCKS] = {};
	struct platform_device *pdev;
	size_t avail, expected, align;
	struct gen_pool *pool;
	struct resource res;
	int errors = 0;
	int i, n = 0;

	if (of_address_to_resource(np, 0, &res)) {
		pr_err("FAIL: %pOF has no address\n", np);
		return 1;
	}

	pdev = of_find_device_by_node(np);
	pool = pdev ? gen_pool_get(&pdev->dev, NULL) : NULL;
	if (!pool) {
		pr_err("FAIL: no SRAM pool for %pOF %pR\n", np, &res);
		errors++;
		goto out;
	}

	align = 1UL << pool->min_alloc_order;
	avail = gen_pool_avail(pool);
	pr_info("SRAM pool %pOF %pR: %zu bytes, %zu available\n",
		np, &res, gen_pool_size(pool), avail);

	for (i = 0; i < NUM_BLOCKS; i++) {
		size_t size;

		if (i < ARRAY_SIZE(block_sizes))
			size = block_sizes[i];
		else
			size = rounddown(min_t(size_t, avail / 2, LARGE_BLOCK_MAX), align);
		if (!size)
			continue;

		blocks[n].size = size;
		blocks[n].vaddr = gen_pool_alloc(pool, blocks[n].size);
		if (!blocks[n].vaddr) {
			pr_err("FAIL: unable to allocate %zu bytes\n", blocks[n].size);
			errors++;
			goto free;
		}
		blocks[n].phys = gen_pool_virt_to_phys(pool, blocks[n].vaddr);
		errors += check_block(pool, &res, blocks, n);
		n++;
	}

	expected = 0;
	for (i = 0; i < n; i++)
		expected += ALIGN(blocks[i].size, align);
	if (avail - gen_pool_avail(pool) != expected) {
		pr_err("FAIL: allocated %zu bytes, but the pool shrank by %zu bytes\n",
			expected, avail - gen_pool_avail(pool));
		errors++;
	}

	if (!errors) {
		fill(blocks, n, 0);
		errors += verify(blocks, n, 0);
		fill(blocks, n, 0xffffffff);
		errors += verify(blocks, n, 0xffffffff);
	}

free:
	for (i = 0; i < n; i++)
		gen_pool_free(pool, blocks[i].vaddr, blocks[i].size);

	if (gen_pool_avail(pool) != avail) {
		pr_err("FAIL: %zu bytes were available before the test and %zu after\n",
			avail, gen_pool_avail(pool));
		errors++;
	}

	pr_info("%s: %pOF: %d blocks of 32 bytes to %zu bytes\n",
		errors ? "FAIL" : "PASS", np, n, n ? blocks[n - 1].size : 0);

out:
	if (pdev)
		put_device(&pdev->dev);
	return errors ? 1 : 0;
}

static int __init hello_l2_alloc_init(void)
{
	struct device_node *np;
	int tested = 0;
	int failed = 0;

	pr_info("------Start testing L2 SRAM allocation------\n");

	for_each_compatible_node(np, NULL, "mmio-sram") {
		if (!of_device_is_available(np))
			continue;
		tested++;
		failed += test_pool(np);
	}

	if (!tested) {
		pr_info("SKIP: no mmio-sram pool in the device tree\n");
		return -ENODEV;
	}

	if (failed) {
		pr_err("SUMMARY: %d of %d SRAM pools failed\n", failed, tested);
		return -EIO;
	}

	pr_info("SUMMARY: all %d SRAM pools passed\n", tested);
	return 0;
}

static void __exit hello_l2_alloc_exit(void)
{
}

module_init(hello_l2_alloc_init);
module_exit(hello_l2_alloc_exit);

MODULE_LICENSE("GPL");
MODULE_DESCRIPTION("ADI L2 SRAM allocation test");
MODULE_AUTHOR("HFeng <huanhuan.feng@analog.com>");
