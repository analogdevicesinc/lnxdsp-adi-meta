# Introduction

This page provides instructions for using U-Boot Falcon Mode/ADI Fastboot on ADSP-SC59X boards.

| :memo:        | The Falcon Mode feature is supported on versions Linux for ADSP-SC5xx Processors 3.0.0 and later, and on SC59x.|
|---------------|:------------------------|

## What is Falcon Mode?

It is described in the upstream repo of U-Boot: [U-Boot Falcon Mode](https://github.com/ARM-software/u-boot/blob/master/doc/README.falcon). In short, it's a way to shorten the boot time by allowing the SPL (Secondary Program Loader) to boot the kernel directly, without loading the full bootloader.

## Requirements

You must use Linux for ADSP-SC5xx Processors 3.0.0 or later on an SC59x board.

# Build U-Boot with Falcon Mode support

When following the Getting Start guides, and specifically right after sourcing the setup script, i.e.

```Shell
$ source setup-environment -m adsp-sc598-som-ezkit
```

a build folder along with a local build configuration file is created, under ``$PROJECT_DIR/build/conf/local.conf``. Edit this file with your favourite text editor, and append the following two lines to it:

```Shell
MACHINE_FEATURES:remove = " spl"
MACHINE_FEATURES:append = " falcon"
```

and proceed to building and flashing the image, as usual and as seen on the Getting Started guides.


# Booting with Falcon Mode

When U-Boot with Falcon Mode support has been built and flashed onto the board, the default behaviour will be booting the default mode (SPI boot, if unchanged) straight from the SPL, e.g.:

```Shell
U-Boot SPL 2020.10 (Apr 12 2023 - 18:48:14 +0000)
ADI Boot Mode: 0x1 (QSPI Master)
Trying to boot from SPI
## Loading kernel from FIT Image at 96000000 ...
   Using 'conf-1' configuration
   Verifying Hash Integrity ... OK
   Trying 'kernel-1' kernel subimage
Verifying Hash Integrity ... sha1+ sha1,rsa2048:dev- OK
   Loading kernel from 0x960000e0 to 0x9a200000
   Uncompressing Kernel Image
## Loading ramdisk from FIT Image at 96000000 ...
   Using 'conf-1' configuration
   Verifying Hash Integrity ... OK
   Trying 'ramdisk-3' ramdisk subimage
   Verifying Hash Integrity ... sha1+ sha1,rsa2048:dev- OK
   Loading ramdisk from 0x965a1e50 to 0x9c000000
## Loading fdt from FIT Image at 96000000 ...
   Using 'conf-1' configuration 
   Verifying Hash Integrity ... OK
   Trying 'fdt-2' fdt subimage  
   Verifying Hash Integrity ... sha1+ sha1,rsa2048:dev- OK
   Loading fdt from 0x9659ac00 to 0x99000000
[    0.000000] Booting Linux on physical CPU 0x0000000000 [0x412fd050]
[    0.000000] Linux version 5.15.78-yocto-standard (oe-user@oe-host) (aarch64-poky-linux-musl-gcc (GCC) 11.2.0, GNU ld (GNU Binutils) 2.38.20220313) #1 SMP PREEMPT Wed Apr 12 17:53:07 UTC 2023
[    0.000000] Machine model: ADI 64-bit SC598 SOM EZ Kit
[    0.000000] earlycon: adi_uart0 at MMIO 0x0000000031003000 (options '')
[    0.000000] printk: bootconsole [adi_uart0] enabled
[    0.000000] efi: UEFI not found.
```

You can still fall back to U-Boot Proper by holding down the push button ``PB1`` on the target (carrier) board during reset/booting:

<img src="https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/94b75504-bbf0-42d1-ad60-df2f37aba745" width="600">

You will then be able to use the full U-Boot:

```Shell
U-Boot SPL 2020.10 (Apr 12 2023 - 18:48:14 +0000)
Pushbutton helding during boot -- entering U-Boot ProperADI Boot Mode: 0x1 (QSPI Master)
Trying to boot from BOOTROM
 
 
U-Boot 2020.10 (Apr 12 2023 - 18:48:14 +0000)
 
CPU:   ADSP ADSP-SC598-0.0 (spi slave boot)
Model: ADI sc598-som-ezkit
DRAM:  224 MiB
WDT:   Not found!
MMC:   mmc@310C7000: 0
Loading Environment from SPIFlash... SF: Detected is25lp512 with page size 256 Bytes, erase size 64 KiB, total 64 MiB
OK
In:    serial@0x31003000
Out:   serial@0x31003000
Err:   serial@0x31003000
Net:   eth0: eth0
Hit any key to stop autoboot:  0
```


# ADI Fastboot

Yocto release 5.0.0 introduces ADI Fastboot, which utilizes u-boot falcon mode and kernel modifications to produce the fastest possible boot times, allowing the hardware to be initialized as soon as possible for userspace applications to be run.

Currently Supported boards:
* ADSP-SC598-SOM-EZKIT

## U-boot modifications

SOMs with the EZKIT carrier will default to utilizing the OSPI flash (device 0) on bus 0 as the SPI flash memory of choice for booting (Standard configuration utilizes QSPI present on the SOM). This is coupled with DMA and highest possible frequencies to achieve extremely fast transactions. 

Due to this, we need to flash the OSPI located on the EZKIT carrier via uboot.

Once u-boot is loaded via GDB, proceed with the following:
```
=> run update_spi_uboot_only
=> setenv sfdev 0:0; run update_spi_fit
```

This will now result in a flashed uboot on qspi and the fitImage on the OSPI

## Linux kernel modifications

Linux kernel has been trimmed to achieve a minimal overall boot time. The following drivers/devices have been disabled:

* Crypto (CRC and PKTE)
* SHARC (both cores and remoteproc)
* eMMC
* QSPI
* USB
* gptimers
* TRU
* HADC
* SPORT
* Ethernet
* SRAM
* Watchdog

Following is what an expected kernel boot should be:

```Shell
[2025-08-08 12:17:04.393] ADI Boot Mode: 0x1 (QSPI Master)                                                                                                                                                                                                                                                           [122/1838][2025-08-08 12:17:04.398] Trying to boot from SPI
[2025-08-08 12:17:04.398] Probe @41666666Hz
[2025-08-08 12:17:04.415] ?6
                                                                                          [2025-08-08 12:17:04.513] Read ID via 1x SPI: c2 85 3b
[2025-08-08 12:17:04.513] Configure MX66LM1G45: OPI DTR = on
[2025-08-08 12:17:04.519] Read ID via 8x OPI+DTR: c2 85 3b
[2025-08-08 12:17:04.519]       Success: ID Match!
[2025-08-08 12:17:04.519] Configuration Register 2(0x0): 2
[2025-08-08 12:17:04.524]       Success: In DTR OPI Mode!
[2025-08-08 12:17:04.524] ## Loading kernel from FIT Image at 96000000 ...
[2025-08-08 12:17:04.530]    Using 'conf-1' configuration
[2025-08-08 12:17:04.536]    Trying 'kernel-1' kernel subimage
[2025-08-08 12:17:04.536]    Loading kernel from 0x960000e0 to 0x9a200000
[2025-08-08 12:17:04.541]    Uncompressing Kernel Image
[2025-08-08 12:17:06.332] ## Loading ramdisk from FIT Image at 96000000 ...
[2025-08-08 12:17:06.332]    Using 'conf-1' configuration
[2025-08-08 12:17:06.338]    Trying 'ramdisk-3' ramdisk subimage
[2025-08-08 12:17:06.338]    Loading ramdisk from 0x963f63b8 to 0x9c000000
[2025-08-08 12:17:06.383] ## Loading fdt from FIT Image at 96000000 ...
[2025-08-08 12:17:06.383]    Using 'conf-1' configuration
[2025-08-08 12:17:06.388]    Trying 'fdt-2' fdt subimage
[2025-08-08 12:17:06.388]    Loading fdt from 0x963ef428 to 0x99000000
[2025-08-08 12:17:06.442] Booting Linux on physical CPU 0x0000000000 [0x412fd050]
[2025-08-08 12:17:06.442] Linux version 6.12.0-yocto-standard-00079-g89984f318ee7 (oe-user@oe-host) (aarch64-adi_glibc-linux-gcc (GCC) 13.2.0, GNU ld (GNU Bin5
[2025-08-08 12:17:06.464] Machine model: ADI 64-bit SC598 SOM EZ Kit
[2025-08-08 12:17:06.464] earlycon: adi_uart0 at MMIO 0x0000000031003000 (options '')
[2025-08-08 12:17:06.470] printk: legacy bootconsole [adi_uart0] enabled
[2025-08-08 12:17:06.476] efi: UEFI not found.
[2025-08-08 12:17:06.476] OF: reserved mem: 0x0000000020000000..0x00000000200003ff (1 KiB) nomap non-reusable rsc_tbl0@20000000
[2025-08-08 12:17:06.487] OF: reserved mem: 0x0000000020000400..0x00000000200007ff (1 KiB) nomap non-reusable rsc_tbl0@20000400
[2025-08-08 12:17:06.493] OF: reserved mem: 0x0000000020005000..0x0000000020024fff (128 KiB) nomap non-reusable sharc_internal_icc@20005000
[2025-08-08 12:17:06.504] OF: reserved mem: 0x0000000020040000..0x000000002007ffff (256 KiB) map non-reusable sram1-reserved@20040000
[2025-08-08 12:17:06.515] OF: reserved mem: 0x0000000020080000..0x0000000020083fff (16 KiB) nomap non-reusable vdev0vring0@20080000
[2025-08-08 12:17:06.526] Reserved memory: created DMA memory pool at 0x0000000020084000, size 0 MiB
[2025-08-08 12:17:06.532] OF: reserved mem: initialized node vdev0buffer@20084000, compatible id shared-dma-pool
[2025-08-08 12:17:06.537] OF: reserved mem: 0x0000000020084000..0x00000000200a3fff (128 KiB) nomap non-reusable vdev0buffer@20084000
[2025-08-08 12:17:06.548] OF: reserved mem: 0x00000000200a4000..0x00000000200a7fff (16 KiB) nomap non-reusable vdev0vring0@200A4000
[2025-08-08 12:17:06.560] Reserved memory: created DMA memory pool at 0x00000000200a8000, size 0 MiB
[2025-08-08 12:17:06.565] OF: reserved mem: initialized node vdev0buffer@200A8000, compatible id shared-dma-pool
[2025-08-08 12:17:06.570] OF: reserved mem: 0x00000000200a8000..0x00000000200c7fff (128 KiB) nomap non-reusable vdev0buffer@200A8000
[2025-08-08 12:17:06.588] NUMA: Faking a node at [mem 0x0000000020040000-0x000000009dffffff]
[2025-08-08 12:17:06.594] NODE_DATA(0) allocated [mem 0x9df60480-0x9df626bf]
[2025-08-08 12:17:06.594] Zone ranges:
[2025-08-08 12:17:06.599]   DMA      [mem 0x0000000020040000-0x000000009dffffff]
[2025-08-08 12:17:06.599]   DMA32    empty
[2025-08-08 12:17:06.605]   Normal   empty
[2025-08-08 12:17:06.605] Movable zone start for each node
[2025-08-08 12:17:06.610] Early memory node ranges
[2025-08-08 12:17:06.610]   node   0: [mem 0x0000000020040000-0x000000002007ffff]
[2025-08-08 12:17:06.616]   node   0: [mem 0x0000000090000000-0x000000009dffffff]
[2025-08-08 12:17:06.621] Initmem setup node 0 [mem 0x0000000020040000-0x000000009dffffff]
[2025-08-08 12:17:06.627] On node 0, zone DMA: 64 pages in unavailable ranges
[2025-08-08 12:17:06.649] On node 0, zone DMA: 32640 pages in unavailable ranges
[2025-08-08 12:17:06.655] On node 0, zone DMA: 8192 pages in unavailable ranges
[2025-08-08 12:17:06.655] percpu: Embedded 29 pages/cpu s78176 r8192 d32416 u118784
[2025-08-08 12:17:06.661] Detected VIPT I-cache on CPU0
[2025-08-08 12:17:06.666] CPU features: detected: GIC system register CPU interface
[2025-08-08 12:17:06.672] CPU features: detected: Virtualization Host Extensions
[2025-08-08 12:17:06.677] CPU features: detected: ARM errata 1165522, 1319367, or 1530923
[2025-08-08 12:17:06.683] alternatives: applying boot alternatives
[2025-08-08 12:17:06.683] Kernel command line: earlycon=adi_uart,0x31003000 console=ttySC0,115200 vmalloc=512M
[2025-08-08 12:17:06.694] Unknown kernel command line parameters "vmalloc=512M", will be passed to user space.
[2025-08-08 12:17:06.700] Dentry cache hash table entries: 32768 (order: 6, 262144 bytes, linear)
[2025-08-08 12:17:06.706] Inode-cache hash table entries: 16384 (order: 5, 131072 bytes, linear)
[2025-08-08 12:17:06.711] Fallback order for Node 0: 0
[2025-08-08 12:17:06.717] Built 1 zonelists, mobility grouping on.  Total pages: 57408
[2025-08-08 12:17:06.722] Policy zone: DMA
[2025-08-08 12:17:06.722] mem auto-init: stack:all(zero), heap alloc:off, heap free:off
[2025-08-08 12:17:06.728] software IO TLB: SWIOTLB bounce buffer size adjusted to 0MB
[2025-08-08 12:17:06.734] software IO TLB: area num 1.
[2025-08-08 12:17:06.734] software IO TLB: mapped [mem 0x000000009de8d000-0x000000009decd000] (0MB)
[2025-08-08 12:17:06.761] SLUB: HWalign=64, Order=0-3, MinObjects=0, CPUs=1, Nodes=1
[2025-08-08 12:17:06.761] trace event string verifier disabled
[2025-08-08 12:17:06.767] rcu: Preemptible hierarchical RCU implementation.
[2025-08-08 12:17:06.772] rcu:  RCU restricting CPUs from NR_CPUS=512 to nr_cpu_ids=1.
[2025-08-08 12:17:06.778]       Trampoline variant of Tasks RCU enabled.
[2025-08-08 12:17:06.778] rcu: RCU calculated value of scheduler-enlistment delay is 25 jiffies.
[2025-08-08 12:17:06.789] rcu: Adjusting geometry for rcu_fanout_leaf=16, nr_cpu_ids=1
[2025-08-08 12:17:06.795] RCU Tasks: Setting shift to 0 and lim to 1 rcu_task_cb_adjust=1 rcu_task_cpu_ids=1.
[2025-08-08 12:17:06.806] NR_IRQS: 64, nr_irqs: 64, preallocated irqs: 0
[2025-08-08 12:17:06.812] GICv3: GIC: Using split EOI/Deactivate mode
[2025-08-08 12:17:06.812] GICv3: 384 SPIs implemented
[2025-08-08 12:17:06.812] GICv3: 0 Extended SPIs implemented
[2025-08-08 12:17:06.817] Root IRQ handler: gic_handle_irq
[2025-08-08 12:17:06.822] GICv3: GICv3 features: 16 PPIs
[2025-08-08 12:17:06.822] GICv3: GICD_CTRL.DS=0, SCR_EL3.FIQ=0
[2025-08-08 12:17:06.828] GICv3: CPU0: found redistributor 0 region 0:0x0000000031240000
[2025-08-08 12:17:06.833] rcu: srcu_init: Setting srcu_struct sizes based on contention.
[2025-08-08 12:17:06.840] arch_timer: cp15 timer(s) running at 31.25MHz (phys).
[2025-08-08 12:17:06.840] clocksource: arch_sys_counter: mask: 0xffffffffffffff max_cycles: 0xe6a171046, max_idle_ns: 881590405314 ns
[2025-08-08 12:17:06.851] sched_clock: 56 bits at 31MHz, resolution 32ns, wraps every 4398046511088ns
[2025-08-08 12:17:06.857] clocksource: cs_adi_gptimer: mask: 0xffffffff max_cycles: 0xffffffff, max_idle_ns: 15290083572 ns
[2025-08-08 12:17:06.868] sched_clock: 32 bits at 125MHz, resolution 8ns, wraps every 17179869180ns
[2025-08-08 12:17:06.874] Console: colour dummy device 80x25
[2025-08-08 12:17:06.880] Calibrating delay loop (skipped), value calculated using timer frequency.. 62.50 BogoMIPS (lpj=125000)
[2025-08-08 12:17:06.885] pid_max: default: 32768 minimum: 301
[2025-08-08 12:17:06.891] LSM: initializing lsm=capability
[2025-08-08 12:17:06.891] Mount-cache hash table entries: 512 (order: 0, 4096 bytes, linear)
[2025-08-08 12:17:06.896] Mountpoint-cache hash table entries: 512 (order: 0, 4096 bytes, linear)
[2025-08-08 12:17:06.910] cacheinfo: Unable to detect cache hierarchy for CPU 0
[2025-08-08 12:17:06.910] rcu: Hierarchical SRCU implementation.
[2025-08-08 12:17:06.915] rcu:  Max phase no-delay instances is 1000.
[2025-08-08 12:17:06.921] EFI services will not be available.
[2025-08-08 12:17:06.921] smp: Bringing up secondary CPUs ...
[2025-08-08 12:17:06.927] smp: Brought up 1 node, 1 CPU
[2025-08-08 12:17:06.927] SMP: Total of 1 processors activated.
[2025-08-08 12:17:06.939] CPU: All CPU(s) started at EL2
[2025-08-08 12:17:06.939] CPU features: detected: 32-bit EL0 Support
[2025-08-08 12:17:06.939] CPU features: detected: Data cache clean to the PoU not required for I/D coherence
[2025-08-08 12:17:06.949] CPU features: detected: CRC32 instructions
[2025-08-08 12:17:06.949] CPU features: detected: RCpc load-acquire (LDAPR)
[2025-08-08 12:17:06.955] CPU features: detected: LSE atomic instructions
[2025-08-08 12:17:06.960] CPU features: detected: Speculative Store Bypassing Safe (SSBS)
[2025-08-08 12:17:06.966] alternatives: applying system-wide alternatives
[2025-08-08 12:17:06.972] Memory: 206140K/229632K available (5568K kernel code, 812K rwdata, 1892K rodata, 1856K init, 479K bss, 21468K reserved, 0K cma-reser)
[2025-08-08 12:17:06.984] devtmpfs: initialized
[2025-08-08 12:17:06.993] clocksource: jiffies: mask: 0xffffffff max_cycles: 0xffffffff, max_idle_ns: 7645041785100000 ns
[2025-08-08 12:17:06.999] futex hash table entries: 256 (order: 2, 16384 bytes, linear)
[2025-08-08 12:17:07.004] 30064 pages in range for non-PLT usage
[2025-08-08 12:17:07.010] 521584 pages in range for PLT usage
[2025-08-08 12:17:07.010] pinctrl core: initialized pinctrl subsystem
[2025-08-08 12:17:07.016] DMI not present or invalid.
[2025-08-08 12:17:07.016] DMA: preallocated 128 KiB GFP_KERNEL pool for atomic allocations
[2025-08-08 12:17:07.022] DMA: preallocated 128 KiB GFP_KERNEL|GFP_DMA pool for atomic allocations
[2025-08-08 12:17:07.033] DMA: preallocated 128 KiB GFP_KERNEL|GFP_DMA32 pool for atomic allocations
[2025-08-08 12:17:07.039] thermal_sys: Registered thermal governor 'step_wise'
[2025-08-08 12:17:07.045] cpuidle: using governor menu
[2025-08-08 12:17:07.045] hw-breakpoint: found 6 breakpoint and 4 watchpoint registers.
[2025-08-08 12:17:07.050] ASID allocator initialised with 65536 entries
[2025-08-08 12:17:07.057] Serial: AMBA PL011 UART driver
[2025-08-08 12:17:07.084] HugeTLB: registered 1.00 GiB page size, pre-allocated 0 pages
[2025-08-08 12:17:07.097] HugeTLB: 0 KiB vmemmap can be freed for a 1.00 GiB page
[2025-08-08 12:17:07.106] HugeTLB: registered 32.0 MiB page size, pre-allocated 0 pages
[2025-08-08 12:17:07.115] HugeTLB: 0 KiB vmemmap can be freed for a 32.0 MiB page
[2025-08-08 12:17:07.124] HugeTLB: registered 2.00 MiB page size, pre-allocated 0 pages
[2025-08-08 12:17:07.138] HugeTLB: 0 KiB vmemmap can be freed for a 2.00 MiB page
[2025-08-08 12:17:07.138] HugeTLB: registered 64.0 KiB page size, pre-allocated 0 pages
[2025-08-08 12:17:07.143] HugeTLB: 0 KiB vmemmap can be freed for a 64.0 KiB page
[2025-08-08 12:17:07.150] ACPI: Interpreter disabled.
[2025-08-08 12:17:07.156] iommu: Default domain type: Translated
[2025-08-08 12:17:07.156] iommu: DMA domain TLB invalidation policy: strict mode
[2025-08-08 12:17:07.162] usbcore: registered new interface driver usbfs
[2025-08-08 12:17:07.167] usbcore: registered new interface driver hub
[2025-08-08 12:17:07.173] usbcore: registered new device driver usb
[2025-08-08 12:17:07.173] i2c-adi-twi 31001400.twi: ADI on-chip I2C TWI Controller, regs_base@(____ptrval____)
[2025-08-08 12:17:07.198] i2c-adi-twi 31001600.twi: ADI on-chip I2C TWI Controller, regs_base@(____ptrval____)
[2025-08-08 12:17:07.204] clocksource: Switched to clocksource arch_sys_counter
[2025-08-08 12:17:07.210] VFS: Disk quotas dquot_6.6.0
[2025-08-08 12:17:07.210] VFS: Dquot-cache hash table entries: 512 (order 0, 4096 bytes)
[2025-08-08 12:17:07.220] pnp: PnP ACPI: disabled
[2025-08-08 12:17:07.230] Unpacking initramfs...
[2025-08-08 12:17:07.241] workingset: timestamp_bits=42 max_order=16 bucket_order=0
[2025-08-08 12:17:07.333] Block layer SCSI generic (bsg) driver version 0.4 loaded (major 251)
[2025-08-08 12:17:07.350] io scheduler mq-deadline registered
[2025-08-08 12:17:07.350] io scheduler kyber registered
[2025-08-08 12:17:07.364] adi-dma 31022000.dma: Creating new peripheral DMA controller instance
[2025-08-08 12:17:07.380] adi-dma 31023000.dma: Creating new peripheral DMA controller instance
[2025-08-08 12:17:07.396] adi-dma 3102d000.dma: Creating new peripheral DMA controller instance
[2025-08-08 12:17:07.419] adi-dma 310a7000.dma: Creating new peripheral DMA controller instance
[2025-08-08 12:17:07.436] adi-dma 31026000.dma: Creating new peripheral DMA controller instance
[2025-08-08 12:17:07.462] adi-dma 3109a000.dma: Creating new MDMA controller instance
[2025-08-08 12:17:07.477] adi-trigger-routing-unit 3108a000.tru: Connecting master 134 to slave 160
[2025-08-08 12:17:07.494] adi-trigger-routing-unit 3108a000.tru: Connecting master 135 to slave 164
[2025-08-08 12:17:07.506] adi-trigger-routing-unit 3108a000.tru: Connecting master 136 to slave 168
[2025-08-08 12:17:07.528] Serial: 8250/16550 driver, 4 ports, IRQ sharing enabled
[2025-08-08 12:17:07.539] ADI serial driver
[2025-08-08 12:17:07.546] adi-uart4 31003000.uart: Serial probe
[2025-08-08 12:17:07.553] Freeing initrd memory: 2800K
[2025-08-08 12:17:07.553] 31003000.uart: ttySC0 at MMIO 0x0 (irq = 0, base_baud = 7812500) is a ADI-UART4
[2025-08-08 12:17:07.558] printk: legacy console [ttySC0] enabled
[2025-08-08 12:17:07.564] printk: legacy console [ttySC0] enabled
[2025-08-08 12:17:07.569] printk: legacy bootconsole [adi_uart0] disabled
[2025-08-08 12:17:07.569] printk: legacy bootconsole [adi_uart0] disabled
[2025-08-08 12:17:07.593] adi-spi3 31030000.spi: registered ADI SPI controller spi2
[2025-08-08 12:17:07.593] VFIO - User Level meta-driver version: 0.3
[2025-08-08 12:17:07.599] input: adp5587 as /devices/platform/scb/31001600.twi/i2c-1/1-0034/input/input0
[2025-08-08 12:17:07.626] adp5588_keys 1-0034: Rev.4 controller
[2025-08-08 12:17:07.626] i2c_dev: i2c /dev entries driver
[2025-08-08 12:17:07.635] hw perfevents: enabled with armv8_pmuv3 PMU driver, 7 (0,8000003f) counters available
[2025-08-08 12:17:07.656] Demotion targets for Node 0: null
[2025-08-08 12:17:07.665] adsp-sru-ctrl 310ca000.sru-ctrl-dai1: Started without extended selection codes (SC573, SC584, SC589)
[2025-08-08 12:17:07.672] clk: Disabling unused clocks
[2025-08-08 12:17:07.678] PM: genpd: Disabling unused power domains
[2025-08-08 12:17:07.685] Freeing unused kernel memory: 1856K
[2025-08-08 12:17:07.685] Run /init as init process
[2025-08-08 12:17:07.772]
[2025-08-08 12:17:07.772]          Analog Initial Ram Filesystem
[2025-08-08 12:17:07.772]                 www.analog.com
[2025-08-08 12:17:07.777]               www.yoctoproject.org
[2025-08-08 12:17:07.777]
[2025-08-08 12:17:07.777] Analog [Initramfs]: Preparing Operating System....
[2025-08-08 12:17:07.783] Analog [Initramfs]: Mounting Root File System...
[2025-08-08 12:17:07.804] Analog [Initramfs]: No root device found, dropping to getty
[2025-08-08 12:17:07.821] udhcpc: socket: Function not implemented
[2025-08-08 12:17:07.929]
[2025-08-08 12:17:07.929] adsp-sc598-som-ezkit login:
```

Approximate expected boot time is ~3-4s after plugging the board to power/reset.