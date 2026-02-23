===============================================
Booting Linux from SPI Flash on SC58x & SC573
===============================================

.. note::

   The instructions are applicable to Linux for ADSP-SC5xx Processors 3.0.0 or later.

The ADSP-SC58x-EZKIT & ADSP-SC573-EZKIT come equipped with 16 MiB of SPI Flash. The ``adsp-sc5xx-tiny`` image can be stored into it and used to
boot Linux from it. To do that, you need to change the Libc
implementation from the default GNU to musl, to further reduce the
image’s size.

Change the Libc implementation
------------------------------

Changing the Libc implementation is a simple edit in the
``$YOUR_PROJECT_DIR/build/conf/local.conf`` file, from
``DISTRO ?= "adi-distro-glibc"`` to ``DISTRO ?= "adi-distro-musl"``.

Build the tiny image
--------------------

Following the Quickstart guide for your target board in the `Landing
Pages <https://github.com/analogdevicesinc/lnxdsp-adi-meta/wiki>`__, use

.. code:: shell

   $ bitbake adsp-sc5xx-tiny

Build and install the SDK
-------------------------

+--------------------------+--------------------------------------------+
| :memo:                   | The ADSP-SC584-EZKIT has been chosen as    |
|                          | the example for these instructions, and    |
|                          | will appear on filenames and paths etc.    |
+==========================+============================================+
+--------------------------+--------------------------------------------+

The SDK will provide you with the cross toolchain needed to develop
application for the target board, alongside various miscellaneous tools.
Notably, it will provide you with OpenOCD and GDB, which you can use to
run and flash U-Boot on the board.

The SDK can be built for the adsp-sc5xx-minimal image or the
adsp-sc5xx-full image. To build the SDK for the adsp-sc5xx-minimal image
invoke bitbake from within the build directory created previously.

.. code:: shell

   $ bitbake adsp-sc5xx-minimal -c populate_sdk

or for the adsp-sc5xx-full image

.. code:: shell

   $ bitbake adsp-sc5xx-full -c populate_sdk

When the build has completed you will find a set of files in the
/tmp/deploy/sdk directory. For example, the minimal image on
SC584-EZKIT:

.. code:: shell

   $ ls tmp/deploy/sdk
   adi-distro-musl-x86_64-adsp-sc5xx-minimal-cortexa5t2hf-neon-adsp-sc584-ezkit-toolchain-3.0.0.host.manifest
   adi-distro-musl-x86_64-adsp-sc5xx-minimal-cortexa5t2hf-neon-adsp-sc584-ezkit-toolchain-3.0.0.sh
   adi-distro-musl-x86_64-adsp-sc5xx-minimal-cortexa5t2hf-neon-adsp-sc584-ezkit-toolchain-3.0.0.target.manifest
   adi-distro-musl-x86_64-adsp-sc5xx-minimal-cortexa5t2hf-neon-adsp-sc584-ezkit-toolchain-3.0.0.testdata.json

The
``adi-distro-glibc-glibc-x86_64-adsp-sc5xx-minimal-cortexa5t2hf-neon-adsp-sc584-ezkit-toolchain-3.0.0.sh``
is a self-extracting archive containing the SDK.

Invoke the self-extracting archive. It will default to installing to
/opt/adi-distro-musl/3.0.0 but gives you the option to select your own
install folder during the installation. For the minimal image on
SC589-EZKIT

.. code:: shell

   $ ./adi-distro-musl-x86_64-adsp-sc5xx-minimal-cortexa5t2hf-neon-adsp-sc589-ezkit-toolchain-3.0.0.sh
   Analog Devices Inc Reference Distro SDK installer version 3.0.0
   ===============================================================
   Enter target directory for SDK (default: /opt/adi-distro-musl/3.0.0):
   You are about to install the SDK to "/opt/adi-distro-musl/3.0.0". Proceed [Y/n]? y
   Extracting SDK........................................................................done
   Setting it up...done
   SDK has been successfully set up and is ready to be used.
   Each time you wish to use the SDK in a new shell session, you need to source the environment setup script e.g.

    $ . /opt/adi-distro-musl/3.0.0/environment-setup-cortexa5t2hf-neon-adi-linux-musleabi
   Your SDK is now installed.

Running U-Boot on the Board for the first time
----------------------------------------------

+--------------------------+--------------------------------------------+
| :memo:                   | It’s always good practice to erase the     |
|                          | contents of ``/tftpboot/`` before running  |
|                          | and/or flashing a new build of U-Boot or   |
|                          | Linux. You can do so by executing          |
|                          | ``rm /tftpboot/*`` before proceeding       |
+==========================+============================================+
+--------------------------+--------------------------------------------+

Copy the U-Boot binary & loader files to the tftp directory:

.. code:: shell

   $ cp tmp/deploy/images/adsp-sc584-ezkit/u-boot-spl-sc584-ezkit.elf /tftpboot/ 
   $ cp tmp/deploy/images/adsp-sc584-ezkit/u-boot-proper-sc584-ezkit.elf /tftpboot/ 
   $ cp tmp/deploy/images/adsp-sc584-ezkit/stage1-boot.ldr /tftpboot/ 
   $ cp tmp/deploy/images/adsp-sc584-ezkit/stage2-boot.ldr /tftpboot/ 

Before installing the software on to the development board, ensure that
the following cables are connected: \* Board connected to network via
ethernet cable using J13 connector. \* Board connected to host PC using
USB micro cable, connected to USB/UART port on the development board \*
Board connected to the ICE 1000 or ICE 2000 via the DEBUG port on the
board \* ICE is also connected to host PC via USB mini cable

-  The BOOT MODE selector on the SC584 board should be turned to “0”.

The console output from U-Boot and later on Linux will appear on the USB
serial port configured in minicom earlier so open up minicom.
``Terminal1: minicom``

.. code:: shell

   $ sudo minicom

In a separate console launch OpenOCD and connect to the development
board.

``Terminal2: OpenOCD``

.. code:: shell

   $ sdk_usr=/opt/adi-distro-glibc/3.0.0/sysroots/x86_64-adi_glibc_sdk-linux/usr/
   $ $sdk_usr/bin/openocd -f $sdk_usr/share/openocd/scripts/interface/<ICE>.cfg -f $sdk_usr/share/openocd/scripts/target/adspsc58x.cfg

Where ``<ICE>`` should be replaced with ``ice1000`` or ``ice2000``
depending on your hardware.

When successful you should see a message similar to the console output
below

``Terminal2: OpenOCD``

.. code:: shell

   Open On-Chip Debugger (PKGVERSION)  OpenOCD 0.10.0-g40378454d (2023-04-05-10:35)
   Licensed under GNU GPL v2
   Report bugs to <processor.tools.support@analog.com>
   adapter speed: 1000 kHz
   Info : transports supported by the debug adapter: "jtag", "swd"
   Info : auto-select transport "jtag"
   halt and restart using CTI
   trst_only separate trst_push_pull
   adspsc58x_init
   Info : ICE-1000 firmware version is 1.0.2
   Info : clock speed 1000 kHz
   Info : JTAG tap: adspsc584.adjc tap/device found: 0x428080cb (mfg: 0x065, part: 0x2808, ver: 0x4)
   Info : JTAG tap: adspsc584.dap enabled
   Info : adspsc584.dap: hardware has 3 breakpoints, 2 watchpoints
   Info : adspsc584.dap: but you can only set 1 watchpoint
   Info : accepting 'gdb' connection on tcp/3333
   Info : JTAG tap: adspsc584.adjc tap/device found: 0x428080cb (mfg: 0x065, part: 0x2808, ver: 0x4)
   Info : JTAG tap: adspsc584.dap enabled
   Info : adspsc584.dap: hardware has 3 breakpoints, 2 watchpoints
   Info : adspsc584.dap: but you can only set 1 watchpoint

In a third console window launch GDB and type
``target extended-remote :3333``. This will make GDB to connect to the
gdbserver on the local host using port 3333. Then, load the U-Boot SPL
into RAM by typing ``load``. Hit ``Ctrl+C`` to interrupt thereafter.

``Terminal3: GDB``

.. code:: shell

   $ cd /tftpboot
   $ /opt/adi-distro-glibc/3.0.0/sysroots/x86_64-adi_glibc_sdk-linux/usr/bin/arm-adi_glibc-linux-gnueabi/arm-adi_glibc-linux-gnueabi-gdb u-boot-spl-sc584-ezkit.elf
   ...
   (gdb) target extended-remote :3333
   Remote debugging using :3333
   0x00004884 in ?? ()
   (gdb) load
   Loading section .text, size 0x9c0c lma 0x20080000
   Loading section .rodata, size 0x1198 lma 0x20089c0c
   Loading section .dtb.init.rodata, size 0x1460 lma 0x2008adb0
   Loading section .data, size 0x514 lma 0x2008c210
   Loading section .u_boot_list, size 0xa50 lma 0x2008c724
   Start address 0x20080000, load size 53608
   Transfer rate: 29 KB/sec, 7658 bytes/write.
   (gdb) c
   Continuing.
   ^C
   Program received signal SIGINT, Interrupt.

You will see a message on Terminal 1 running minicom, informing you that
you can now load U-Boot Proper

``Terminal1: minicom``

.. code:: shell

   U-Boot SPL 2020.10 (Mar 16 2023 - 13:07:24 +0000)
   ADI Boot Mode: 0x0 (JTAG/BOOTROM)
   SPL execution has completed.  Please load U-Boot Proper via JTAG

Now, load U-Boot Proper into RAM.

``Terminal3: GDB``

.. code:: shell

   (gdb) load u-boot-proper-sc584-ezkit.elf
   Loading section .text, size 0x3a8 lma 0x89200000
   Loading section .text_rest, size 0x46084 lma 0x892003c0
   Loading section .rodata, size 0xf56c lma 0x89246444
   Loading section .hash, size 0x18 lma 0x892559b0
   Loading section .dtb.init.rodata, size 0x19d0 lma 0x892559d0
   Loading section .data, size 0x22d8 lma 0x892573a0
   Loading section .got.plt, size 0xc lma 0x89259678
   Loading section .u_boot_list, size 0x1644 lma 0x89259684
   Loading section .rel.dyn, size 0x9c98 lma 0x8925acc8
   Loading section .dynsym, size 0x30 lma 0x89264960
   Loading section .dynstr, size 0x1 lma 0x89264990
   Loading section .dynamic, size 0x90 lma 0x89264994
   Loading section .gnu.hash, size 0x18 lma 0x89264a24
   Start address 0x89200000, load size 412185
   Transfer rate: 30 KB/sec, 11776 bytes/write.
   (gdb) c
   Continuing.

At this point U-Boot will now be running in RAM on your target board.
You should see U-Boot booting in the minicom console (Terminal 1). Press
a key to interrupt the boot process before the countdown terminates:
``Terminal1: minicom``

.. code:: shell

   U-Boot 2020.10 (Mar 16 2023 - 13:07:24 +0000)

   CPU:   ADSP ADSP-SC584-0.1 (spi flash boot)
   Detected Revision: 1.1
   Model: ADI sc584-ezkit
   DRAM:  112 MiB
   WDT:   Not found!
   MMC:
   Loading Environment from SPIFlash... SF: Detected w25q128 with page size 256 Bytes, erase size 4 KiB, total 16 MiB
   *** Warning - bad CRC, using default environment

   In:    serial@0x31003000
   Out:   serial@0x31003000
   Err:   serial@0x31003000
   Net:   eth0: eth0
   Hit any key to stop autoboot:  0
   => 

Flash U-Boot to SPI Flash
~~~~~~~~~~~~~~~~~~~~~~~~~

In the U-Boot console, set the IP address of the Linux PC that hosts the
U-Boot loader files (``stage1-boot.ldr`` & ``stage2-boot.ldr``) on TFTP.
``Terminal1: minicom``

.. code:: shell

   => setenv serverip <SERVERIP>
   => setenv tftpserverip <SERVERIP>

+--------------------------+--------------------------------------------+
| :memo:                   | To find the IP address of your host Linux  |
|                          | PC you can issue the ``ip addr`` command   |
|                          | from the shell or console.                 |
+==========================+============================================+
+--------------------------+--------------------------------------------+

If your network **supports** DHCP, run:

.. code:: shell

   => dhcp

If your network **does NOT support** DHCP, in the U-Boot console
configure the board IP address you want the board to be assigned with
(``<IPADDR>``) and remove “``dhcp;``” from the “``init_ethernet``”
command:

.. code:: shell

   => setenv ipaddr <IPADDR>
   => edit init_ethernet
   => edit: mii info; <remove "dhcp;" from here>; setenv serverip ${tftpserverip};
   => saveenv

i.e. ``init_ethernet`` should now be
``init_ethernet=mii info; setenv serverip ${tftpserverip};``, where
prior to this change it was
``init_ethernet=mii info; dhcp; setenv serverip ${tftpserverip};``

+--------------------------+--------------------------------------------+
| :memo:                   | If flashing a board that had been          |
|                          | previously programmed, it’s good to erase  |
|                          | the whole flash before as sometimes        |
|                          | previous U-Boot installations might leave  |
|                          | remnants. You can do that by typing        |
|                          | ``=>                                       |
|                          |  sf probe ${sfdev}; sf erase 0 0x4000000`` |
|                          | on the U-Boot prompt before proceeding to  |
|                          | the following instructions                 |
+==========================+============================================+
+--------------------------+--------------------------------------------+

Next, run the U-Boot update command to copy the U-Boot loader files from
the host PC to the target board, and write it into flash:

.. code:: shell

   => run update_spi_uboot_only

You will see an output similar to the one below:

.. code:: shell

   => run update_spi_uboot_only
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.33.116; our IP address is 10.37.33.113
   Filename 'stage1-boot.ldr'.
   Load address: 0x89000000
   Loading: ####
            2.2 MiB/s
   done
   Bytes transferred = 53684 (d1b4 hex)
   SF: Detected w25q128 with page size 256 Bytes, erase size 4 KiB, total 16 MiB
   device 0 offset 0x0, size 0xd1b4
   SF: 53684 bytes @ 0x0 Written: OK
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.33.116; our IP address is 10.37.33.113
   Filename 'stage2-boot.ldr'.
   Load address: 0x89000000
   Loading: #############################
            2 MiB/s
   done
   Bytes transferred = 412460 (64b2c hex)
   SF: Detected w25q128 with page size 256 Bytes, erase size 4 KiB, total 16 MiB
   device 0 offset 0x20000, size 0x64b2c
   SF: 412460 bytes @ 0x20000 Written: OK

In order to store the ``serverip`` and the DHCP or otherwise assigned IP
address of the board and have them available on next boot, you can run
the following command:

.. code:: shell

   => saveenv
   Saving Environment to SPIFlash... Erasing SPI flash...Writing to SPI flash...done
   OK

At this point the U-Boot binary is stored in flash. You can now
disconnect the ICE-1000 or ICE-2000 from the development board and make
sure to switch the BMODE to position 1. You will only need to reconnect
this if your board fails to boot and you need to re-follow these
instructions.

Booting Linux from SPI Flash
----------------------------

You’d first need to copy the fitImage and the ``tiny`` root filesystem
on the TFTP server directory of the Host machine:

.. code:: shell

   $ cp tmp/deploy/images/adsp-sc584-ezkit/fitImage /tftpboot/
   $ cp tmp/deploy/images/adsp-sc584-ezkit/adsp-sc5xx-tiny-adsp-sc584-ezkit.jffs2 /tftpboot/

In order to flash the kernel on the flash, run the below command and
observe a similar output:

.. code:: shell

   => run update_spi_fit
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.33.116; our IP address is 10.37.33.113
   Filename 'fitImage'.
   Load address: 0x89000000
   Loading: #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            ################################
            2.2 MiB/s
   done
   Bytes transferred = 6192194 (5e7c42 hex)
   SF: Detected w25q128 with page size 256 Bytes, erase size 4 KiB, total 16 MiB
   device 0 offset 0xe0000, size 0x5e7c42
   SF: 6192194 bytes @ 0xe0000 Written: OK

Now run the following command to also flash the root filesystem:

.. code:: shell

   => run update_spi_rfs
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.33.116; our IP address is 10.37.33.113
   Filename 'adsp-sc5xx-tiny-adsp-sc584-ezkit.jffs2'.
   Load address: 0x89000000
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
            ##
            2.1 MiB/s
   done
   Bytes transferred = 9568256 (920000 hex)
   SF: Detected w25q128 with page size 256 Bytes, erase size 4 KiB, total 16 MiB
   device 0 offset 0x6e0000, size 0x920000
   SF: 9568256 bytes @ 0x6e0000 Written: OK

You are now ready to boot into Linux, by entering ``run spiboot`` on the
U-Boot prompt:

.. code:: shell

   => run spiboot
   SF: Detected w25q128 with page size 256 Bytes, erase size 4 KiB, total 16 MiB
   device 0 offset 0xe0000, size 0x5e7c42
   SF: 6192194 bytes @ 0xe0000 Read: OK
   ## Loading kernel from FIT Image at 89000000 ...
      Using 'conf-1' configuration
      Verifying Hash Integrity ... OK
      Trying 'kernel-1' kernel subimage
        Description:  Linux kernel
        Type:         Kernel Image
        Compression:  uncompressed
        Data Start:   0x890000dc
        Data Size:    4774688 Bytes = 4.6 MiB
        Architecture: ARM
        OS:           Linux
        Load Address: 0x80008000
        Entry Point:  0x80008000
        Hash algo:    sha1
        Hash value:   0bddc73f1c3fbbd1777023e676edbf51f8cd663f
        Sign algo:    sha1,rsa2048:
        Sign value:   unavailable
      Verifying Hash Integrity ... sha1+ sha1,rsa2048:- OK

   ...

   [  OK  ] Reached target Host and Network Name Lookups.
   [  OK  ] Started D-Bus System Message Bus.
   [  OK  ] Started User Login Management.
   [  OK  ] Reached target Multi-User System.


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

   adsp-sc584-ezkit login: root
   Password: adi
   root@adsp-sc584-ezkit:~#
