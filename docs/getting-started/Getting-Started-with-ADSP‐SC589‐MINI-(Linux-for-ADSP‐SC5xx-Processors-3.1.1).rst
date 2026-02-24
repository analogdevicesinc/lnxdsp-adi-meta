=======================
Setting Up Your Host PC
=======================

The build system is currently supported on host PCs running Ubuntu 20.04 LTS 64-bit. For setting up your host PC, please see the :doc:`Setting Up Your Host PC <Setting-Up-Your-Host-PC>` page.

====================
Fetching the Sources
====================

The source is fully contained in the `Analog Devices Linux for ADSP repositories <https://github.com/analogdevicesinc?q=lnxdsp&type=all&language=&sort=>`_.

To install the sources: 


.. code-block:: shell

   mkdir ~/sc589-mini
   cd ~/sc589-mini
   mkdir bin
   curl http://commondatastorage.googleapis.com/git-repo-downloads/repo > ./bin/repo
   chmod a+x ./bin/repo
   ./bin/repo init \
      -u https://github.com/analogdevicesinc/lnxdsp-repo-manifest.git \
      -b main \
      -m release-3.1.1.xml
   ./bin/repo sync


==================
Building the Image
==================

Preparing the buildtool
=======================

Yocto requires the environment to be configured before building is possible.  A setup-environment script in the sc589-mini folder contains all the required environment settings for your build target.
Source the setup script for your board:

.. code-block:: shell

   source setup-environment -m adsp-sc589-mini


Sourcing the script will configure your build environment and create a build folder along with a local build configuration file.  See the Yocto Manual for further details.


.. note::

   Note that the build environment needs to be sourced once only before building.  If later working in a different terminal, the `setup-environment` script should be sourced again.  If sourcing the `setup-environment` script is done without specifying the machine, Yocto will reuse the previous configuration settings and retain any changes made to the files in the `conf` folder



Building the example
====================

You can build two different versions of the root file system; minimal and full.
To build the example images invoke bitbake from within the build directory created previously.


.. code-block:: shell

   bitbake adsp-sc5xx-minimal
   bitbake adsp-sc5xx-full


When the build completes you will see a warning that the ELF binary has relocations in .text. It is OK to ignore this warning


.. note::

   Building a Linux distribution with Yocto is a significantly demanding process, both in CPU and network usage. A full build from scratch is estimated to take around 170 minutes for an 11th Gen Intel Core i5-11500T with 16 GB of RAM and a stable, fast Internet connection. This estimate can go up significantly for a poorer Internet connection or CPU resources, so set aside plenty of time for a clean build.


Building the SDK
================

The SDK will provide you with the cross toolchain needed to develop application for the target board, alongside various miscellaneous tools. Notably, it will provide you with OpenOCD and GDB, which you can use to run and flash U-Boot on the board.

The SDK can be built for the ``adsp-sc5xx-minimal`` image or the ```adsp-sc5xx-full`` image. To build the SDK for the ``adsp-sc5xx-minimal`` image invoke bitbake from within the build directory created previously.


.. code-block:: shell

   bitbake adsp-sc5xx-minimal -c populate_sdk

or for the adsp-sc5xx-full image

.. code-block:: shell

   bitbake adsp-sc5xx-full -c populate_sdk


When the build has completed you will find a set of files in the ``<BUILD_DIR>/tmp/deploy/sdk`` directory. For example, the minimal image on SC594:

.. code-block:: shell

   ls tmp/deploy/sdk
   adi-distro-glibc-glibc-x86_64-adsp-sc5xx-minimal-cortexa5t2hf-neon-adsp-sc589-mini-toolchain-3.1.1.host.manifest
   adi-distro-glibc-glibc-x86_64-adsp-sc5xx-minimal-cortexa5t2hf-neon-adsp-sc589-mini-toolchain-3.1.1.sh
   adi-distro-glibc-glibc-x86_64-adsp-sc5xx-minimal-cortexa5t2hf-neon-adsp-sc589-mini-toolchain-3.1.1.target.manifest
   adi-distro-glibc-glibc-x86_64-adsp-sc5xx-minimal-cortexa5t2hf-neon-adsp-sc589-mini-toolchain-3.1.1.testdata.json


The ``adi-distro-glibc-glibc-x86_64-adsp-sc5xx-minimal-cortexa5t2hf-neon-adsp-sc589-mini-toolchain-3.1.1.sh`` is a self-extracting archive containing the SDK.

Installing the SDK
==================

Invoke the self-extracting archive.
It will default to installing to ``/opt/adi-distro-glibc/3.1.1`` but gives you the option to select your own install folder during the installation.
For the minimal image on SC594

.. code-block:: shell

   ./adi-distro-glibc-glibc-x86_64-adsp-sc5xx-minimal-cortexa55-adsp-sc589-mini-toolchain-3.1.1.sh
   Analog Devices Inc Reference Distro (glibc) SDK installer version 3.1.1
   =======================================================================
   Enter target directory for SDK (default: /opt/adi-distro-glibc/3.1.1):
   You are about to install the SDK to "/opt/adi-distro-glibc/3.1.1". Proceed [Y/n]?
   Extracting SDK....................................................................................................................done
   Setting it up...done
   SDK has been successfully set up and is ready to be used.
   Each time you wish to use the SDK in a new shell session, you need to source the environment setup script e.g.
    $ . /opt/adi-distro-glibc/3.1.1/environment-setup-cortexa55-adi_glibc-linux


Your SDK is now installed.

==================
Setup the hardware
==================



Before installing the software on to the development board, ensure that the following cables are connected:

.. image:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/f57cd6de-4bbd-4693-a805-66cecf8ea3af
   :width: 400

  * Board connected to network via ethernet cable using J13 connector.
  * Board connected to host PC using USB micro cable, connected to USB/UART port on the development board
  * Board connected to the ICE 1000 or ICE 2000 via the DEBUG port on the board
  * ICE is also connected to host PC via USB mini cable
  * The BOOT MODE selector on the SC589 board should be turned to "0".

.. image:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/7ca2ad5c-d87f-43c5-8f13-b84770aa8def
   :width: 400


==============================================================
Transfer, run and flash U-Boot on the board for the first time
==============================================================



.. note::

   It's always good practice to erase the contents of ``/tftpboot/`` before running and/or flashing a new build of U-Boot or Linux. You can do so by executing ``rm /tftpboot/*`` on your host PC before proceeding


Transfer and run U-Boot on RAM
==============================


Copy the U-Boot binary & loader files to the tftp directory:


.. code-block:: shell

   cp tmp/deploy/images/adsp-sc589-mini/u-boot-proper-sc589-mini.elf /tftpboot/
   cp tmp/deploy/images/adsp-sc589-mini/u-boot-spl-sc589-mini.elf /tftpboot/
   cp tmp/deploy/images/adsp-sc589-mini/stage1-boot.ldr /tftpboot/
   cp tmp/deploy/images/adsp-sc589-mini/stage2-boot.ldr /tftpboot/


The console output from U-Boot and later on Linux will appear on the USB serial port configured in minicom earlier so open up minicom.

```Terminal1: minicom```


.. code-block:: shell

   sudo minicom

  
In a separate console launch OpenOCD and connect to the development board.


```Terminal2: OpenOCD```


.. code-block:: shell

   sdk_usr=/opt/adi-distro-glibc/3.1.1/sysroots/x86_64-adi_glibc_sdk-linux/usr/
   $sdk_usr/bin/openocd -f $sdk_usr/share/openocd/scripts/interface/<ICE>.cfg -f $sdk_usr/share/openocd/scripts/target/adspsc58x.cfg


Where ``<ICE>`` should be replaced with ```ice1000`` or ``ice2000`` depending on your hardware.
When successful you should see a message similar to the console output below

```Terminal2: OpenOCD```


.. code-block:: shell

   Open On-Chip Debugger (PKGVERSION)  OpenOCD 0.10.0-g40378454d (2023-04-05-10:35)
   Licensed under GNU GPL v2
   Report bugs to <processor.tools.support@analog.com>
   adapter speed: 1000 kHz
   Info : transports supported by the debug adapter: "jtag", "swd"
   Info : auto-select transport "jtag"
   halt and restart using CTI
   trst_only separate trst_push_pull
   Info : ICE-1000 firmware version is 1.0.2
   Info : clock speed 1000 kHz
   Info : JTAG tap: adspsc59x.adjc tap/device found: 0x028240cb (mfg: 0x065, part: 0x2824, ver: 0x0)
   Info : JTAG tap: adspsc59x.dap enabled
   Info : adspsc59x.dap: hardware has 3 breakpoints, 2 watchpoints
   Info : adspsc59x.dap: but you can only set 1 watchpoint


In a third console window launch GDB and type ``target extended-remote :3333``. This will make GDB to connect to the gdbserver on the local host using port 3333. Then, load the U-Boot SPL into RAM by typing ```load``. Hit ``Ctrl+C`` to interrupt thereafter.

```Terminal3: GDB```


.. code-block:: shell

   cd /tftpboot
   /opt/adi-distro-glibc/3.1.1/sysroots/x86_64-adi_glibc_sdk-linux/usr/bin/arm-adi_glibc-linux-gnueabi/arm-adi_glibc-linux-gnueabi-gdb u-boot-spl-sc589-mini.elf
   ...
   (gdb) target extended-remote :3333
   Remote debugging using :3333
   0x00005f96 in ?? ()
   (gdb) load
   Loading section .text, size 0xef3c lma 0x20080000
   Loading section .rodata, size 0x204f lma 0x2008ef3c
   Loading section .dtb.init.rodata, size 0x1740 lma 0x20090f90
   Loading section .data, size 0x548 lma 0x200926d0
   Loading section .u_boot_list, size 0xc38 lma 0x20092c18
   Start address 0x20080000, load size 79947
   Transfer rate: 31 KB/sec, 9993 bytes/write.
   (gdb) c
   Continuing.
   ^C
   Program received signal SIGINT, Interrupt.


You will see a message on Terminal 1 running minicom, informing you that you can now load U-Boot Proper

```Terminal1: minicom```


.. code-block:: shell

   U-Boot SPL 2020.10 (Mar 16 2023 - 13:07:24 +0000)
   ADI Boot Mode: 0x0 (JTAG/BOOTROM)
   SPL execution has completed.  Please load U-Boot Proper via JTAG


Now, load U-Boot Proper into RAM.

```Terminal3: GDB```


.. code-block:: shell

   (gdb) load u-boot-proper-sc589-mini.elf
   Loading section .text, size 0x3a8 lma 0xb2200000
   Loading section .efi_runtime, size 0xdf0 lma 0xb22003a8
   Loading section .text_rest, size 0x510f4 lma 0xb22011a0
   Loading section .rodata, size 0x10536 lma 0xb2252298
   Loading section .hash, size 0x18 lma 0xb22627d0
   Loading section .dtb.init.rodata, size 0x1e50 lma 0xb22627f0
   Loading section .data, size 0x2fd8 lma 0xb2264640
   Loading section .got.plt, size 0xc lma 0xb2267618
   Loading section .u_boot_list, size 0x1908 lma 0xb2267624
   Loading section .efi_runtime_rel, size 0xd0 lma 0xb2268f2c
   Loading section .rel.dyn, size 0xaee8 lma 0xb2268ffc
   Loading section .dynsym, size 0x30 lma 0xb2273ee4
   Loading section .dynstr, size 0x1 lma 0xb2273f14
   Loading section .dynamic, size 0x90 lma 0xb2273f18
   Loading section .gnu.hash, size 0x18 lma 0xb2273fa8
   Start address 0xb2200000, load size 475047
   Transfer rate: 31 KB/sec, 11586 bytes/write.
   (gdb) c
   Continuing.


At this point U-Boot will now be running in RAM on your target board. You should see U-Boot booting in the minicom console (Terminal 1). Press a key to interrupt the boot process before the countdown terminates:

```Terminal1: minicom```


.. code-block:: shell

   U-Boot 2020.10 (Mar 16 2023 - 13:07:24 +0000)
    
   CPU:   ADSP ADSP-SC594-0.0 (spi slave boot)
   Detected Revision: 1.1
   Model: ADI sc589-mini
   DRAM:  992 MiB
   WDT:   Not found!
   MMC:
   Loading Environment from SPIFlash... Read ID via 1x SPI: 9d 5a 19
   SF: Detected is25lx256 with page size 256 Bytes, erase size 128 KiB, total 32 MiB
   OK
   In:    serial@0x31003000
   Out:   serial@0x31003000
   Err:   serial@0x31003000
   Net:   eth0: eth0
   Error: eth1 address not set.
    
   Hit any key to stop autoboot:  0
   =>



Flash U-Boot to SPI Flash
=========================


In the U-Boot console, set the IP address of the Linux PC that hosts the U-Boot loader files (``stage1-boot.ldr`` & ``stage2-boot.ldr``) on TFTP.

```Terminal1: minicom```


.. code-block:: shell

   => setenv tftpserverip <SERVERIP>



.. note::

   To find the IP address of your host Linux PC you can issue the ``ip addr`` command from the shell or console.


If your network **supports** DHCP, run:

.. code-block:: shell

   => dhcp


If your network **does NOT support** DHCP, in the U-Boot console configure the board IP address you want the board to be assigned with (``<IPADDR>``) and remove "``dhcp;``" from the "``init_ethernet``" command: 


.. code-block:: shell

   => setenv ipaddr <IPADDR>
   => edit init_ethernet
   => edit: mii info; <remove "dhcp;" from here>; setenv serverip ${tftpserverip};
   => saveenv


i.e. ``init*ethernet`` should now be ``init*ethernet=mii info; setenv serverip ${tftpserverip};``, where prior to this change it was ``init_ethernet=mii info; dhcp; setenv serverip ${tftpserverip};``


.. note::

   If flashing a board that had been previously programmed, it's good to erase the whole flash before as sometimes previous U-Boot installations might leave remnants. You can do that by typing ``=> run erase_spi`` on the U-Boot prompt before proceeding to the following instructions


Next, run the U-Boot update command to copy the U-Boot loader files from the host PC to the target board, and write it into flash:

.. code-block:: shell

   => run update_spi_uboot_only


You will see an output similar to the one below:

.. code-block:: shell

   => run update_spi_uboot_only
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.33.116; our IP address is 10.37.33.113
   Filename 'stage1-boot.ldr'.
   Load address: 0x96000000
   Loading: ########
            4.8 MiB/s
   done
   Bytes transferred = 115008 (1c140 hex)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 64 KiB, total 64 MiB
   device 0 offset 0x0, size 0x1c140
   SF: 115008 bytes @ 0x0 Written: OK
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.33.116; our IP address is 10.37.33.113
   Filename 'stage2-boot.ldr'.
   Load address: 0x96000000
   Loading: ###########################################
            5.3 MiB/s
   done
   Bytes transferred = 629616 (99b70 hex)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 64 KiB, total 64 MiB
   device 0 offset 0x20000, size 0x99b70
   SF: 629616 bytes @ 0x20000 Written: OK


In order to store the ``tftpserverip`` and the DHCP or otherwise assigned IP address of the board and have them available on next boot, you can run the following command:

.. code-block:: shell

   => saveenv
   Saving Environment to SPIFlash... Erasing SPI flash...Writing to SPI flash...done
   OK

At this point the U-Boot binary is stored in flash. You can now disconnect the ICE-1000 or ICE-2000 from the development board and make sure to switch the BMODE to position 1. You will only need to reconnect this if your board fails to boot and you need to re-follow these instructions.


=============
Booting Linux
=============


Booting the minimal image from SPI Flash
========================================

The U-Boot console is used to copy U-Boot (SPL and Proper), the minimal root filesystem image and the fitImage (which contains the kernel image and dtb file) into RAM and then write them to Flash. Copy the required files from ``<BUILD DIR>/tmp/deploy/images`` to your ``/tftpboot`` directory.


.. code-block:: shell

   cp tmp/deploy/images/adsp-sc589-mini/stage1-boot.ldr /tftpboot/
   cp tmp/deploy/images/adsp-sc589-mini/stage2-boot.ldr /tftpboot/
   cp tmp/deploy/images/adsp-sc589-mini/fitImage /tftpboot/
   cp tmp/deploy/images/adsp-sc589-mini/adsp-sc5xx-minimal-adsp-sc589-mini.jffs2 /tftpboot


If your network **supports** DHCP, run:

.. code-block:: shell

   => run update_spi

<details closed>
  <summary>If your network does NOT support DHCP</summary>
in the U-Boot console configure the board IP address you want the board to be assigned with (IPADDR) and remove "dhcp;" from the "init_ethernet" command.


.. code-block:: shell

   => setenv ipaddr <IPADDR>
   => edit init_ethernet
   => edit: mii info; <remove "dhcp;" from here>; setenv serverip ${tftpserverip};
   => saveenv


i.e. ``init*ethernet`` should now be ``init*ethernet=mii info; setenv serverip ${tftpserverip};``, where prior to this change it was ``init_ethernet=mii info; dhcp; setenv serverip ${tftpserverip};``

After editing ``start*update*spi``, proceed to running as ``update_spi``, as above.
</details>

You should see output similar to the following.

.. code-block:: shell

   => run update_spi
   PHY 0x00: OUI = 0x80028, Model = 0x23, Rev = 0x01, 100baseT, FDX
   Speed: 1000, full duplex
   BOOTP broadcast 1
   DHCP client bound to address 10.37.33.113 (90 ms)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 64 KiB, total 64 MiB
   SF: 67108864 bytes @ 0x0 Erased: OK
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.33.116; our IP address is 10.37.33.113
   Filename 'stage1-boot.ldr'.
   Load address: 0x82000000
   Loading: ######
            2.9 MiB/s
   done
   Bytes transferred = 80032 (138a0 hex)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 64 KiB, total 64 MiB
   device 0 offset 0x0, size 0x138a0
   SF: 80032 bytes @ 0x0 Written: OK
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.33.116; our IP address is 10.37.33.113
   Filename 'stage2-boot.ldr'.
   Load address: 0x82000000
   Loading: #################################
            4.7 MiB/s
   done
   Bytes transferred = 475344 (740d0 hex)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 64 KiB, total 64 MiB
   device 0 offset 0x20000, size 0x740d0
   SF: 475344 bytes @ 0x20000 Written: OK
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.33.116; our IP address is 10.37.33.113
   Filename 'fitImage'.
   Load address: 0x82000000
   Loading: #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
    
            5 MiB/s
   done
   Bytes transferred = 6678526 (65e7fe hex)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 64 KiB, total 64 MiB
   device 0 offset 0x100000, size 0x65e7fe
   SF: 6678526 bytes @ 0x100000 Written: OK
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.33.116; our IP address is 10.37.33.113
   Filename 'adsp-sc5xx-minimal-adsp-sc589-mini.jffs2'.
   Load address: 0x82000000
   Loading: #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #####################################################
            5 MiB/s
   done
   Bytes transferred = 21757952 (14c0000 hex)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 64 KiB, total 64 MiB
   device 0 offset 0x900000, size 0x14c0000
   SF: 21757952 bytes @ 0x900000 Written: OK
   Saving Environment to SPIFlash... SF: Detected is25lx256 with page size 256 Bytes, erase size 128 KiB, total 32 MiB
   Erasing SPI flash...Writing to SPI flash...done
   OK
   =>


The U-Boot image, root filesystem and Linux kernel are now stored in SPI Flash. Place the BOOT MODE jumper to **position 1** and press the RESET button, the board should boot into Linux.

.. image:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/4b742143-5039-4a7d-af61-79e306c69aea
   :width: 400

Network Booting
===============


In order to boot Linux via the network, the TFTP server should be setup as indicated in the :doc:`Setting Up Your Host PC <Setting-Up-Your-Host-PC>` page, and a copy of the fitImage should be copied into the ``/tftpboot`` directory.


.. code-block:: shell

   cp tmp/deploy/images/adsp-sc589-mini/fitImage /tftpboot/


NFS Boot
--------


In order to boot Linux via NFS, the NFS server should be setup as indicated in `Setting Up Your Host PC: Configure NFS Server <Setting-Up-Your-Host-PC>`.

The root filesystem should then be copied to ``/romfs``.


.. code-block:: shell

   sudo tar -xf tmp/deploy/images/adsp-sc589-mini/adsp-sc5xx-full-adsp-sc589-mini.tar.xz -C /romfs


Next, on the target, from u-boot, run the following command:

.. code-block:: shell

   => run nfsboot
   ......
   ......
            Starting Update UTMP about System Runlevel Changes...
   [  OK  ] Started Update UTMP about System Runlevel Changes.
   
   
        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        @@@@@@@@  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        @@@@@@@@     @@@@@@@@@@@@@@@@@@@@@@@@@@
        @@@@@@@@        @@@@@@@@@@@@@@@@@@@@@@@
        @@@@@@@@            @@@@@@@@@@@@@@@@@@@
        @@@@@@@@               @@@@@@@@@@@@@@@@
        @@@@@@@@                   @@@@@@@@@@@@
        @@@@@@@@                     @@@@@@@@@@
        @@@@@@@@                        @@@@@@@
        @@@@@@@@                     @@@@@@@@@@
        @@@@@@@@                   @@@@@@@@@@@@
        @@@@@@@@               @@@@@@@@@@@@@@@@
        @@@@@@@@            @@@@@@@@@@@@@@@@@@@
        @@@@@@@@        @@@@@@@@@@@@@@@@@@@@@@@
        @@@@@@@@     @@@@@@@@@@@@@@@@@@@@@@@@@@
        @@@@@@@@  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
   
           Analog Devices Yocto Distribution
                    www.analog.com
                 www.yoctoproject.org
   
   adsp-sc589-mini login: root
   Password: adi
   root@adsp-sc589-mini:~# 


Booting Linux Using SD Card
===========================

Formatting the SD Card
----------------------

In order to use an SD Card with Linux we need to prepare it by formatting it in the correct format. 
To do this, follow the commands below. The example code in this section assumes that the SD Card is reported to be ``/dev/sdb``. Ensure that you change these commands to use your device.


.. note::

   You can use ``sudo fdisk -l`` to list the available devices and partitions, in order to locate the USB device.



.. code-block:: shell

   sudo fdisk /dev/sdb
   /* Create primary partition 1, 256M size*/
   Command (m for help): n
   Select (default p): p
   Partition number (1-4, default 1): 1
   First sector (2048-3887103, default 2048): PRESS ENTER
   Last sector, +sectors or +size{K,M,G} (2048-3887103, default 3887103): PRESS ENTER
   
   /* Save partition */
   Command (m for help): w


Now that the storage device is partitioned, you need to format it to ext4:


.. code-block:: shell

   sudo mkfs.ext4 /dev/sdb1


Writing the Kernel and rootFS to the SD Card
--------------------------------------------


Mount the device to a directory of your liking in your host machine (in the example, ``/mnt``), and run the following commands which will uncompress the root file system and then store the kernel image under ``/boot`` inside it.


.. code-block:: shell

   sudo mkdir /mnt
   sudo mount -t ext4 /dev/sdb1 /mnt
   sudo mkdir /mnt/boot
   sudo cp tmp/deploy/images/adsp-sc589-mini/adsp-sc5xx-ramdisk-adsp-sc589-mini.cpio.xz /mnt/boot/adsp-sc5xx-ramdisk-adsp-sc589-mini.cpio.xz.u-boot
   sudo cp tmp/deploy/images/adsp-sc589-mini/sc589-mini.dtb /mnt/boot/
   sudo cp tmp/deploy/images/adsp-sc589-mini/fitImage /mnt/boot/
   sudo tar -xf tmp/deploy/images/adsp-sc589-mini/adsp-sc5xx-minimal-adsp-sc589-mini.tar.xz -C /mnt
   sudo umount /mnt
   


The file system and kernel image are now installed on to the SD card. The SD card can now be safely removed from the Host PC.

Booting Linux from the SD card
------------------------------


Now, on U-Boot, set the following environment variables:


.. code-block:: shell

   => setenv mmcargs 'setenv bootargs root=/dev/mmcblk0p1 rw rootfstype=ext4 rootwait earlycon=adi_uart,0x31003000 console=ttySC0,115200'
   => setenv mmcboot 'mmc rescan; run mmcload; run mmcargs; bootm ${loadaddr};'
   => setenv mmcload 'ext4load mmc 0:1 ${loadaddr} /boot/${imagefile};'



And type to boot

.. code-block:: shell

   run mmcboot


The linux kernel will then boot up using the file system stored in the SD card.


.. note::

   You can manually change the default boot method U-Boot is going to use upon the next restart and on. To do this, type `=> edit bootcmd`, erase the current boot method and type in your preferred one. E.g. `edit: run spiboot`, erase `spiboot` and type `mmcboot`, `usbboot` or another boot method command of preference, and hit 'Enter'. Save the change by typing `saveenv` and hit 'Enter'. U-Boot will now boot using the SD card if left uninterrupted when loaded upon subsequent boots


Booting Linux from USB Mass Storage
===================================


Formatting the USB storage device
---------------------------------

The first step is to format the USB stick to a format that U-Boot supports. 

To do this, follow the commands below. The example code in this section assumes that the USB device is reported to be ``/dev/sdb``. Ensure that you change these commands to use your device.


.. note::

   You can use ``sudo fdisk -l`` to list the available devices and partitions, in order to locate the USB device.



.. code-block:: shell

   sudo fdisk /dev/sdb
   /* Create primary partition 1, 256M size*/
   Command (m for help): n
   Select (default p): p
   Partition number (1-4, default 1): 1
   First sector (2048-3887103, default 2048): PRESS ENTER
   Last sector, +sectors or +size{K,M,G} (2048-3887103, default 3887103): PRESS ENTER
   
   /* Save partition */
   Command (m for help): w


Now that the storage device is partitioned, you need to format it to ext4:


.. code-block:: shell

   sudo mkfs.ext4 /dev/sdb1


Writing the Kernel and rootFS to the USB storage device
-------------------------------------------------------


Mount the device to a directory of your liking in your host machine (in the example, ``/mnt``), and run the following commands which will uncompress the root file system and then store the kernel image under ``/boot`` inside it.


.. code-block:: shell

   sudo mkdir /mnt
   sudo mount -t ext4 /dev/sdb1 /mnt
   sudo mkdir /mnt/boot
   sudo tar -xf tmp/deploy/images/adsp-sc589-mini/adsp-sc5xx-minimal-adsp-sc589-mini.tar.xz -C /mnt
   sudo cp tmp/deploy/images/adsp-sc589-mini/fitImage /mnt/boot/
   sudo umount /mnt



Booting from the USB storage device
-----------------------------------


Now, on U-Boot, set the following environment variablesL


.. code-block:: shell

   => setenv usbargs 'setenv bootargs root=/dev/sda1 rw rootfstype=ext4 rootwait earlycon=adi_uart,0x31003000 console=ttySC0,115200'
   => setenv usbload 'ext4load usb 0 ${loadaddr} /boot/${imagefile};'
   => setenv usbboot 'usb start; run usbload; run usbargs; bootm ${loadaddr};'


And type to boot

.. code-block:: shell

   => run usbboot


Now the rootfs is set to be your USB storage device, and the amount of space is the size of the partition created earlier on the device.
