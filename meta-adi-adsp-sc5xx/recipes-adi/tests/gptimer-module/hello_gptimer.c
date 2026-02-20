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
#include <mach/hardware.h>
#include <mach/cpu.h>

#include <asm/mach/arch.h>
#include <asm/mach/time.h>
#include <asm/mach/map.h>
#include <asm/mach/irq.h>
#include <mach/hardware.h>
#include <mach/cpu.h>
#include <mach/dma.h>
#ifdef CONFIG_ARCH_SC57X
#include <mach/portmux-sc57x.h>
#endif
#ifdef CONFIG_ARCH_SC58X
#include <mach/portmux-sc58x.h>
#endif
#include <mach/irqs.h>
#include <mach/clkdev.h>

MODULE_LICENSE("Dual BSD/GPL");
#ifdef CONFIG_ARCH_SC57X
struct sc57x_gptimer *timer = NULL;
#endif
#ifdef CONFIG_ARCH_SC58X
struct sc58x_gptimer *timer = NULL;
#endif
    

static int hello_gptimer_init(void)
{
	int id=5;

	printk(KERN_ALERT "========Test gptimer module in kernel space========\n");

	timer=gptimer_request(id);

	printk(KERN_ALERT "id: %d\n", id);
	if (timer == NULL)
		printk(KERN_ALERT "========Test gptimer module request timer(%d) failed\n", id);

        disable_gptimers(1 << id);
	set_gptimer_config(timer, TIMER_OUT_DIS| TIMER_MODE_PWM_CONT | TIMER_PULSE_HI | TIMER_IRQ_PER);
	set_gptimer_period(timer, 0xFFFFFFFF);
	set_gptimer_pwidth(timer, 0xFFFFFFFE);

	enable_gptimers(1 << id);

        get_gptimer_count(timer);

	printk(KERN_ALERT "========Test gptimer module in kernel space: timer (%d) ========\n",get_gptimer_count(timer));

	return 0;
}

static void hello_gptimer_exit(void)
{
        gptimer_free(timer);
	printk(KERN_ALERT "Goodbye, cruel world\n");
}

module_init(hello_gptimer_init);
module_exit(hello_gptimer_exit);

