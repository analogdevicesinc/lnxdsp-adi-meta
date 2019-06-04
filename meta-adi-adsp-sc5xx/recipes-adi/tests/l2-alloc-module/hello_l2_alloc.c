/* Test module for L2 attributes and stuff */

#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/io.h>
#include <linux/device.h>
#include <linux/err.h>
#include <linux/list.h>
#include <linux/of.h>
#include <linux/of_device.h>
#include <linux/platform_device.h>
#include <asm/mach/arch.h>
#include <asm/mach/time.h>
#include <asm/mach/map.h>
#include <mach/hardware.h>
#include <mach/cpu.h>
#include <mach/dma.h>
#include <mach/sram.h>

MODULE_LICENSE("Dual BSD/GPL");

static size_t size[10]={2,4,6,8,10,12,14,16,18,20};

static int hello_l2_alloc_init(void)
{
        int i;
        void *p;
        for(i=0;i<10;i++) { 
        p=sram_alloc(size[i]);
	printk(KERN_ALERT "=====alloc address is %p =======\n",p);
	}
	printk(KERN_ALERT "======== hello_l2_alloc module finished. =======\n");
	return 0;
}

static void hello_l2_alloc_exit(void)
{
    int ret;
        ret=sram_free(&size);
	printk(KERN_ALERT "Goodbye, cruel world\n");
        return;
}

module_init(hello_l2_alloc_init);
module_exit(hello_l2_alloc_exit);

