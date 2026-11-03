// SPDX-License-Identifier: GPL-2.0-only
/*
 * Copyright 2026 Analog Devices Inc.
 *
 * GPTimer test module
 *
 * Runs a general-purpose timer that the kernel does not use as its
 * clocksource or clockevent in continuous PWM mode for 100 ms, and checks
 * that it counted at the rate of the gptimer clock. The timer's previous
 * configuration and run state are restored afterwards. Loading the module
 * runs the test, and the load fails if the test fails.
 */
#include <linux/bits.h>
#include <linux/clk.h>
#include <linux/delay.h>
#include <linux/io.h>
#include <linux/iopoll.h>
#include <linux/irqflags.h>
#include <linux/ktime.h>
#include <linux/math64.h>
#include <linux/module.h>
#include <linux/of.h>
#include <linux/of_address.h>

/* Shared gptimer registers */
#define GPTIMER_RUN		0x04
#define GPTIMER_RUN_SET		0x08
#define GPTIMER_RUN_CLR		0x0C
#define GPTIMER_STOP_CFG_SET	0x14
#define GPTIMER_DATA_ILAT	0x2C

/* Per-timer registers, at the timer's adi,offset */
#define GPTIMER_CFG_OFF		0x00
#define GPTIMER_CNT_OFF		0x04
#define GPTIMER_PER_OFF		0x08
#define GPTIMER_WID_OFF		0x0C
#define GPTIMER_DLY_OFF		0x10

#define TIMER_OUT_DIS		0x0800
#define TIMER_PULSE_HI		0x0080
#define TIMER_IRQ_PER		0x0030
#define TIMER_MODE_PWM_CONT	0x000c

#define TEST_TIME_MS		100
/* Allowed difference between the measured and expected rates, in percent */
#define RATE_TOLERANCE		2

struct timer_state {
	u16 cfg;
	u32 per, wid, dly;
	bool running;
};

/* The configuration registers can only be written while the timer is stopped */
static int stop_timer(void __iomem *base, int id)
{
	u16 run;

	writew(BIT(id), base + GPTIMER_STOP_CFG_SET);
	writew(BIT(id), base + GPTIMER_RUN_CLR);
	return readw_poll_timeout_atomic(base + GPTIMER_RUN, run, !(run & BIT(id)), 1, 1000);
}

static int timer_id = 5;
module_param(timer_id, int, 0444);
MODULE_PARM_DESC(timer_id, "gptimer to test (default 5)");

static struct device_node *find_timer(struct device_node *np, u32 id)
{
	struct device_node *child;
	u32 reg;

	for_each_child_of_node(np, child) {
		if (!of_property_read_u32(child, "reg", &reg) && reg == id)
			return child;
	}

	return NULL;
}

static int __init hello_gptimer_init(void)
{
	struct device_node *np, *timer_np;
	void __iomem *base, *timer;
	u32 offset, count0, count1, ticks;
	u64 elapsed_ns, measured_hz;
	unsigned long rate, flags;
	struct timer_state saved;
	ktime_t t0, t1;
	struct clk *clk;
	int ret = 0;

	pr_info("========Test gptimer module in kernel space========\n");

	np = of_find_compatible_node(NULL, NULL, "adi,sc5xx-gptimers");
	if (!np) {
		pr_err("FAIL: no adi,sc5xx-gptimers node in the device tree\n");
		return -ENODEV;
	}

	timer_np = find_timer(np, timer_id);
	if (!timer_np) {
		pr_err("FAIL: gptimer %d is not in the device tree\n", timer_id);
		ret = -ENODEV;
		goto put_np;
	}

	if (of_property_read_bool(timer_np, "adi,is-clocksource") ||
	    of_property_read_bool(timer_np, "adi,is-clockevent")) {
		pr_err("FAIL: gptimer %d is used by the kernel, choose another with timer_id=\n",
			timer_id);
		ret = -EBUSY;
		goto put_timer;
	}

	if (of_property_read_u32(timer_np, "adi,offset", &offset)) {
		pr_err("FAIL: gptimer %d has no adi,offset\n", timer_id);
		ret = -EINVAL;
		goto put_timer;
	}

	clk = of_clk_get(np, 0);
	if (IS_ERR(clk)) {
		pr_err("FAIL: unable to get the gptimer clock\n");
		ret = PTR_ERR(clk);
		goto put_timer;
	}
	ret = clk_prepare_enable(clk);
	if (ret) {
		pr_err("FAIL: unable to enable the gptimer clock\n");
		goto put_clk;
	}
	rate = clk_get_rate(clk);

	base = of_iomap(np, 0);
	if (!base) {
		pr_err("FAIL: unable to map the gptimer registers\n");
		ret = -ENOMEM;
		goto disable_clk;
	}
	timer = base + offset;

	/* The kernel starts every gptimer in the device tree, so save its state */
	saved.running = readw(base + GPTIMER_RUN) & BIT(timer_id);
	saved.cfg = readw(timer + GPTIMER_CFG_OFF);
	saved.per = readl(timer + GPTIMER_PER_OFF);
	saved.wid = readl(timer + GPTIMER_WID_OFF);
	saved.dly = readl(timer + GPTIMER_DLY_OFF);

	if (stop_timer(base, timer_id)) {
		pr_err("FAIL: gptimer %d did not stop\n", timer_id);
		ret = -EIO;
		goto unmap;
	}

	writew(TIMER_OUT_DIS | TIMER_MODE_PWM_CONT | TIMER_PULSE_HI | TIMER_IRQ_PER,
	       timer + GPTIMER_CFG_OFF);
	writel(0xFFFFFFFF, timer + GPTIMER_PER_OFF);
	writel(0xFFFFFFFE, timer + GPTIMER_WID_OFF);
	writel(0, timer + GPTIMER_DLY_OFF);
	writew(BIT(timer_id), base + GPTIMER_RUN_SET);

	local_irq_save(flags);
	t0 = ktime_get();
	count0 = readl(timer + GPTIMER_CNT_OFF);
	local_irq_restore(flags);
	msleep(TEST_TIME_MS);
	local_irq_save(flags);
	count1 = readl(timer + GPTIMER_CNT_OFF);
	t1 = ktime_get();
	local_irq_restore(flags);

	if (stop_timer(base, timer_id))
		pr_warn("gptimer %d did not stop after the test\n", timer_id);
	writew(BIT(timer_id), base + GPTIMER_DATA_ILAT);

	writew(saved.cfg, timer + GPTIMER_CFG_OFF);
	writel(saved.per, timer + GPTIMER_PER_OFF);
	writel(saved.wid, timer + GPTIMER_WID_OFF);
	writel(saved.dly, timer + GPTIMER_DLY_OFF);
	if (saved.running)
		writew(BIT(timer_id), base + GPTIMER_RUN_SET);

	ticks = count1 - count0;
	elapsed_ns = ktime_to_ns(ktime_sub(t1, t0));
	measured_hz = div64_u64((u64)ticks * NSEC_PER_SEC, elapsed_ns);

	pr_info("gptimer %d: counted %u ticks in %llu ns: %llu Hz, clock rate %lu Hz\n",
		timer_id, ticks, elapsed_ns, measured_hz, rate);

	if (!rate || abs_diff(measured_hz, (u64)rate) * 100 > (u64)rate * RATE_TOLERANCE) {
		pr_err("FAIL: gptimer %d rate is not within %d%% of the clock rate\n",
			timer_id, RATE_TOLERANCE);
		ret = -EIO;
	} else {
		pr_info("PASS: gptimer %d counts at the clock rate\n", timer_id);
	}

unmap:
	iounmap(base);
disable_clk:
	clk_disable_unprepare(clk);
put_clk:
	clk_put(clk);
put_timer:
	of_node_put(timer_np);
put_np:
	of_node_put(np);
	return ret;
}

static void __exit hello_gptimer_exit(void)
{
	pr_info("Goodbye, cruel world\n");
}

module_init(hello_gptimer_init);
module_exit(hello_gptimer_exit);

MODULE_LICENSE("GPL");
MODULE_DESCRIPTION("ADI gptimer test");
