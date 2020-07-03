/* Test module for L2 attributes and stuff
 *
 * Copyright 2019 Analog Devices Inc.
 *
 * Licensed under the GPL-2 or later.
 */

#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/io.h>
#include <linux/device.h>
#include <linux/err.h>
#include <linux/list.h>
#include <linux/of.h>
#include <linux/of_device.h>
#include <linux/genalloc.h>
#include <linux/platform_device.h>
#include <asm/mach/arch.h>
#include <asm/mach/time.h>
#include <asm/mach/map.h>
#include <mach/hardware.h>
#include <mach/cpu.h>
#include <mach/dma.h>
#include <mach/sram.h>

#define ALLOC_NUM 10
static struct gen_pool *pool;
static unsigned int vaddr[ALLOC_NUM];
/* The minimal size of the pool chunk is 2^min_alloc_order equals to 32 Bytes */
static size_t size[ALLOC_NUM]={32,2*32,4*32,6*32,8*32,10*32,12*32,14*32,16*32,18*32};

static const struct of_device_id hello_l2_alloc_of_match[] = {
	{ .compatible = "adi,hello_l2_alloc" },
	{ },
};

static int hello_l2_alloc_probe(struct platform_device *pdev)
{
    int i;
	const struct of_device_id *match;
	struct device *dev;

	dev = &pdev->dev;

	match = of_match_device(of_match_ptr(hello_l2_alloc_of_match), &pdev->dev);
	if(!match) {
		pr_err("No hello_l2_alloc device defined in dts file\n");
		return -ENODEV;
	}

	pool = of_gen_pool_get(dev->of_node, "adi,sram", 0);
	if (!pool) {
		pr_err("Unable to get sram pool!\n");
		return -ENODEV;
	}

    for(i = 0; i < 10; i++) {
		vaddr[i] = gen_pool_alloc(pool, size[i]);
        printk(KERN_INFO "alloc address: 0x%x, size: 0x%x\n", vaddr[i], size[i]);
    }

    printk(KERN_INFO "hello_l2_alloc module finished.\n");
    return 0;
}

static int hello_l2_alloc_remove(struct platform_device *pdev)
{
    int i;
	for (i = 0; i < 10; i++) {
		gen_pool_free(pool, vaddr[i], size[i]);
        printk(KERN_INFO "free address: 0x%x, size: 0x%x\n", vaddr[i], size[i]);
	}

    printk(KERN_INFO "Goodbye, cruel world\n");
    return 0;
}

static struct platform_driver hello_l2_alloc_driver = {
     .probe = hello_l2_alloc_probe,
     .remove = hello_l2_alloc_remove,
     .driver = {
         .name = "hello_l2_alloc",
         .of_match_table = of_match_ptr(hello_l2_alloc_of_match),
     },
};

module_platform_driver(hello_l2_alloc_driver);
MODULE_DESCRIPTION("ADI L2 Alloc Test Driver");
MODULE_AUTHOR("HFeng <huanhuan.feng@analog.com>");
MODULE_LICENSE("Dual BSD/GPL");
