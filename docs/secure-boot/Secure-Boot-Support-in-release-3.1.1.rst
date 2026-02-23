====================
Secure Boot Support
====================

Introduction
============

This guide helps you build and deploy a security-hardened Linux distribution for ADSP-SC5xx processors with secure boot capabilities. Secure boot ensures that only authenticated software runs on your hardware, protecting against unauthorized firmware modifications and establishing a chain of trust from the bootloader through the Linux kernel.

By following this guide, you'll create a complete secure boot environment that integrates OP-TEE (Open Portable Trusted Execution Environment), ARM Trusted Firmware-A, and Mbed-TLS into your U-Boot and Linux images. The system will verify cryptographic signatures at each boot stage, ensuring the integrity of your software stack.

**What you'll learn:**

* Setting up the secure boot build environment
* Generating and managing cryptographic keys for signing
* Building security-focused Linux images with OP-TEE and ARM TF-A
* Programming one-time programmable (OTP) secure boot keys
* Flashing signed bootloader and kernel images
* Signing and loading secure SHARC+ firmware
* Testing the secure environment with OP-TEE and Mbed-TLS test suites

**Prerequisites:**

* CCES (CrossCore Embedded Studio) installed on your host machine—this guide references version 2.12.1, but you can use newer versions by updating version numbers in commands
* Standard Linux build environment configured—see :doc:`Setting Up Your Host PC <../getting-started/Setting-Up-Your-Host-PC>`
* Understanding of PKI concepts (public/private keys, certificates, signing)
* Development board with ICE-1000 or ICE-2000 debugger

**Important security considerations:**

* OTP key programming is irreversible—test thoroughly with unsigned images first
* Keep private keys secure—loss or compromise requires new hardware
* This guide uses test keys for demonstration—never use test keys in production
* Secure boot alone does not guarantee system security—implement defense in depth

Getting Started
===============

Prerequisites
-------------

System requirements and dependencies are the same as the standard (non-secure) distribution, with the addition of CCES for signing tools.

Build System Setup
==================


*These steps can be skipped if you have previously followed one of the Getting Started guides, such as :doc:`Getting Started with ADSP‐SC598 (Linux for ADSP‐SC5xx Processors 3.1.1) <../getting-started/Getting-Started-with-ADSP‐SC598-(Linux-for-ADSP‐SC5xx-Processors-3.1.1)>`*.

Fetch and install the sources:


.. code-block:: shell

   mkdir ~/gxp2
   cd ~/gxp2
   mkdir bin
   curl http://commondatastorage.googleapis.com/git-repo-downloads/repo > ./bin/repo
   chmod a+x ./bin/repo
   ./bin/repo init \
      -u https://github.com/analogdevicesinc/lnxdsp-repo-manifest.git \
      -b develop/3.1.1-security \
      -m release-3.1.1.xml
   ./bin/repo sync


Key Generation
==============


* Create directory to store the keys


.. code-block:: shell

   sudo mkdir /opt/adi-testkeys
   
   # ensures the normal user can read/write the directory
   sudo chown $(whoami):$(whoami) /opt/adi-testkeys


* Generate key Pair for LDR images signing using ``adi_signtool``


.. code-block:: shell

   cd /opt/adi-testkeys
   /opt/analog/cces/2.12.1/adi_signtool genkeypair -algo ecdsa256 -outfile testkey.der


* Generate key and certificate for verified boot


.. code-block:: shell

   openssl genrsa -F4 -out dev.key 2048
   openssl req -batch -new -x509 -key dev.key -out dev.crt


Build Linux image and SDK
=========================


* Prepare the build work directory:


.. code-block:: shell

   source setup-environment --machine adsp-sc598-som-ezkit --distro adi-security --builddir build-security


* Add the following lines into ``conf/local.conf``:


.. code-block:: shell

   ADI_SIGNTOOL_KEY="/opt/adi-testkeys/testkey.der"
   ADI_SIGNTOOL_PATH="/opt/analog/cces/2.12.1/adi_signtool"
   
   UBOOT_SIGN_KEYDIR = "/opt/adi-testkeys/"
   UBOOT_SIGN_KEYNAME = "dev"
   UBOOT_MKIMAGE_DTCOPTS = "-I dts -O dtb -p 2000"


* The complete system (Linux Image, root filesystem and bootloader) can now be built with:


.. code-block:: shell

   bitbake adsp-sc5xx-minimal


Building the SDK
----------------


* Build the SDK with:


.. code-block:: shell

   bitbake adsp-sc5xx-minimal -c populate_sdk


* It can then be installed by invoking the self-extracting archive, as follows:


.. code-block:: shell

   cd tmp/deploy/sdk
   sudo ./adi-security-glibc-x86_64-adsp-sc5xx-minimal-cortexa55-adsp-sc598-som-ezkit-toolchain-3.1.1.sh
   Analog Devices Inc Reference Distro (glibc) SDK installer version 3.1.1
   =======================================================================
   Enter target directory for SDK (default: /opt/adi-security/3.1.1):
   You are about to install the SDK to "/opt/adi-security/3.1.1". Proceed [Y/n]? y
   Extracting SDK.......................................................................................................................done
   Setting it up...done
   SDK has been successfully set up and is ready to be used.
   Each time you wish to use the SDK in a new shell session, you need to source the environment setup script e.g.
    $ . /opt/adi-security/3.1.1/environment-setup-cortexa55-adi_glibc-linux


Setup the hardware
==================



Before installing the software on to the development board, ensure that the following cables are connected:

<img src="https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/5471efe3-9cb0-44a1-a96e-72f453e1431f" width="400">

  * Board connected to network via ethernet cable using J13 connector.
  * Board connected to host PC using USB micro cable, connected to USB/UART port on the development board
  * Board connected to the ICE 1000 or ICE 2000 via the DEBUG port on the board
  * ICE is also connected to host PC via USB mini cable


On the carrier board is a set of micro switches labelled SW1.  These should all be set to the OFF position before continuing.

<img src="https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/5e285aff-e67d-4d9c-8157-7f596c32134b" width="400">


  * The Power jumper JP1 on the EV-SC598-SOM board should be fitted so that it shorts the two pins closest to the edge.  This will enable the routing of power from the SOMCRR-EZKIT.

  * The BOOT MODE selector on the EV-SC598-SOM board should be turned to "0".

<img src="https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/16242018-95c7-4494-a471-d088a5f52d33" width="400">


==============================================================
Transfer, run and flash U-Boot on the board for the first time
==============================================================



.. list-table::
   :header-rows: 1

   * - :memo:
     - It's always good practice to erase the contents of ``/tftpboot/`` before running and/or flashing a new build of U-Boot or Linux. You can do so by executing ``rm /tftpboot/*`` on your host PC before proceeding


Transfer and run U-Boot on RAM
==============================


Copy the U-Boot binary & loader files to the tftp directory:


.. code-block:: shell

   cp tmp/deploy/images/adsp-sc598-som-ezkit/u-boot-proper-sc598-som-ezkit.elf /tftpboot/
   cp tmp/deploy/images/adsp-sc598-som-ezkit/u-boot-spl-sc598-som-ezkit.elf /tftpboot/
   cp tmp/deploy/images/adsp-sc598-som-ezkit/stage1-boot.ldr /tftpboot/
   cp tmp/deploy/images/adsp-sc598-som-ezkit/stage1-boot-unsigned.ldr /tftpboot/
   cp tmp/deploy/images/adsp-sc598-som-ezkit/stage2-boot.ldr /tftpboot/
   cp tmp/deploy/images/adsp-sc598-som-ezkit/stage2-boot-unsigned.ldr /tftpboot/


The console output from U-Boot and later on Linux will appear on the USB serial port configured in minicom earlier so open up minicom.

```Terminal1: minicom```


.. code-block:: shell

   sudo minicom


In a separate console launch OpenOCD and connect to the development board.


```Terminal2: OpenOCD```


.. code-block:: shell

   sdk_usr=/opt/adi-security/3.1.1/sysroots/x86_64-adi_glibc_sdk-linux/usr/
   $sdk_usr/bin/openocd -f $sdk_usr/share/openocd/scripts/interface/<ICE>.cfg -f $sdk_usr/share/openocd/scripts/target/adspsc59x_a55.cfg

Where ```<ICE>```` should be replaced with ````ice1000```` or ````ice2000``` depending on your hardware.
When successful you should see a message similar to the console output below

```Terminal2: OpenOCD```


.. code-block:: shell

   Open On-Chip Debugger (PKGVERSION)  OpenOCD 0.10.0-gf73da81ab (2024-01-31-15:39)
   Licensed under GNU GPL v2
   Report bugs to <processor.tools.support@analog.com>
   Info : only one transport option; autoselect 'jtag'
   adapter speed: 5000 kHz
   
   Info : halt and restart using CTI enabled
   Info : Listening on port 6666 for tcl connections
   Info : Listening on port 4444 for telnet connections
   Info : ICE-1000 firmware version is 1.0.2
   Info : clock speed 5000 kHz
   Info : JTAG tap: adspsc59x.adjc tap/device found: 0x0282e0cb (mfg: 0x065 (Analog Devices), part: 0x282e, ver: 0x0)
   Info : JTAG tap: adspsc59x.cpu enabled
   Info : DAP adspsc59x.cpu DPIDR indicates ADIv6 protocol is being used
   Info : adspsc59x.cpu: hardware has 6 breakpoints, 4 watchpoints
   Info : starting gdb server for adspsc59x.cpu on 3333
   Info : Listening on port 3333 for gdb connections


In a third console window launch GDB and type ```target extended-remote :3333````. This will make GDB to connect to the gdbserver on the local host using port 3333. Then, load the U-Boot SPL into RAM by typing ````load````. Hit ````Ctrl+C``` to interrupt thereafter.

```Terminal3: GDB```


.. code-block:: shell

   cd /tftpboot
   /opt/adi-security/3.1.1/sysroots/x86_64-adi_glibc_sdk-linux/usr/bin/aarch64-adi_glibc-linux/aarch64-adi_glibc-linux-gdb u-boot-spl-sc598-som-ezkit.elf
   ...
   (gdb) target extended-remote :3333
   Remote debugging using :3333
   0x000000000000352c in ?? ()
   (gdb) load
   Loading section .text, size 0xe430 lma 0x20080000Loading section .rodata, size 0x1d97 lma 0x2008e430
   Loading section .dtb.init.rodata, size 0x1a50 lma 0x200901d0
   Loading section .data, size 0xad1 lma 0x20091c20
   Loading section __u_boot_list, size 0x1468 lma 0x200926f8
   Start address 0x0000000020080000, load size 80720
   Transfer rate: 29 KB/sec, 10090 bytes/write.
   (gdb) c
   Continuing.


You will see a message on Terminal 1 running minicom, informing you that you can now load U-Boot Proper

```Terminal1: minicom```


.. code-block:: shell

   U-Boot SPL 2023.04 (Sep 21 2023 - 13:39:40 +0000)
   ADI Boot Mode: 0x0 (JTAG/BOOTROM)
   SPL execution has completed.  Please load U-Boot Proper via JTAG


Now, press Ctrl-C to halt SPL, and load U-Boot Proper into RAM.

```Terminal3: GDB```


.. code-block:: shell

   ^C
   Program received signal SIGINT, Interrupt.
   ...
   (gdb) load u-boot-proper-sc598-som-ezkit.elf
   Loading section .text, size 0x188 lma 0x96000000
   Loading section .efi_runtime, size 0xb80 lma 0x96000188
   Loading section .text_rest, size 0x6c348 lma 0x96001000
   Loading section .rodata, size 0x15ee9 lma 0x9606d348
   Loading section .hash, size 0x18 lma 0x96083238
   Loading section .dtb.init.rodata, size 0x2290 lma 0x96083250
   Loading section .data, size 0x4678 lma 0x960854e0
   Loading section .got, size 0x8 lma 0x96089b58
   Loading section .got.plt, size 0x18 lma 0x96089b60
   Loading section __u_boot_list, size 0x3b40 lma 0x96089b78
   Loading section .efi_runtime_rel, size 0x1b0 lma 0x9608d6b8
   Loading section .rela.dyn, size 0xd8f0 lma 0x9608d868
   Start address 0x0000000096000000, load size 634457
   Transfer rate: 28 KB/sec, 13217 bytes/write.
   (gdb) c
   Continuing.


At this point U-Boot will now be running in RAM on your target board. You should see U-Boot booting in the minicom console (Terminal 1). Press a key to interrupt the boot process before the countdown terminates:

```Terminal1: minicom```


.. code-block:: shell

   U-Boot 2023.04 (Dec 11 2023 - 16:04:11 +0000)
   
   CPU:   ADSP ADSP-SC598-0.0 (spi slave boot)
   Model: ADI sc598-som-ezkit
   DRAM:  224 MiB
   Core:  142 devices, 22 uclasses, devicetree: embed
   MMC:   mmc@310C7000: 0
   Loading Environment from SPIFlash... SF: Detected is25lp512 with page size 256 Bytes, erase size 4 KiB, total 64 MiB
   OK
   In:    serial@0x31003000
   Out:   serial@0x31003000
   Err:   serial@0x31003000
   Net:   eth0: eth0
   Hit any key to stop autoboot:  0
   =>



Flash (Unsigned) U-Boot to SPI Flash
====================================


In the U-Boot console, set the IP address of the Linux PC that hosts the U-Boot loader files (```stage1-boot.ldr````, ````stage1-boot-unsigned.ldr````, ````stage2-boot.ldr````, ````stage2-boot-unsigned.ldr```) on TFTP.

**Note:** We are going to use **stage1-boot-unsigned.ldr** and **stage2-boot-unsigned.ldr** to boot the device then burn the secure boot key. This is needed only for the first time secure boot key burning. For the next flash you should use **stage1-boot.ldr** and **stage2-boot.ldr**

```Terminal1: minicom```


.. code-block:: shell

   => setenv tftpserverip <SERVERIP>



.. list-table::
   :header-rows: 1

   * - :memo:
     - To find the IP address of your host Linux PC you can issue the ``ip addr`` command from the shell or console.


If your network **supports** DHCP, run:

.. code-block:: shell

   => dhcp


If your network **does NOT support** DHCP, run:

.. code-block:: shell

   => set ipaddr <ADDR>


Where ```<ADDR>``` is the IP address you want to assign.

Next, run the U-Boot update command to copy the U-Boot loader files from the host PC to the target board, and write it into flash:

.. code-block:: shell

   => sf probe 2:1
   => sf erase 0x0 0x01a0000
   => setenv stage1file stage1-boot-unsigned.ldr
   => setenv stage2file stage2-boot-unsigned.ldr
   => run update_spi_uboot_only


You will see an output similar to the one below:

.. code-block:: shell

   => run update_spi_uboot_only
   PHY 0x00: OUI = 0x80028, Model = 0x23, Rev = 0x01, 100baseT, FDX
   Speed: 1000, full duplex
   BOOTP broadcast 1
   BOOTP broadcast 2
   BOOTP broadcast 3
   BOOTP broadcast 4
   BOOTP broadcast 5
   BOOTP broadcast 6
   BOOTP broadcast 7
   DHCP client bound to address 10.37.65.95 (7845 ms)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 4 KiB, total 64 MiB
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.65.102; our IP address is 10.37.65.95
   Filename 'stage1-boot-unsigned.ldr'.
   Load address: 0x90000000
   Loading: ######
            4.5 MiB/s
   done
   Bytes transferred = 80832 (13bc0 hex)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 4 KiB, total 64 MiB
   device 0 offset 0x0, size 0x13bc0
   0 bytes written, 80832 bytes skipped in 0.35s, speed 2178209 B/s
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.65.102; our IP address is 10.37.65.95
   Filename 'stage2-boot-unsigned.ldr'.
   Load address: 0x90000000
   Loading: #################################################################
            ###########
            6.2 MiB/s
   done
   Bytes transferred = 1113216 (10fc80 hex)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 4 KiB, total 64 MiB
   device 0 offset 0x40000, size 0x10fc80
   0 bytes written, 1113216 bytes skipped in 0.484s, speed 2335928 B/s
   Saving Environment to SPIFlash... Erasing SPI flash...Writing to SPI flash...done
   OK
   =>


At this point the U-Boot binary is stored in flash. You can now disconnect the ICE-1000 or ICE-2000 from the development board and make sure to switch the BMODE to position 1. You will only need to reconnect this if your board fails to boot and you need to re-follow these instructions. **Do not reset the board at this stage.**

=============
Booting Linux
=============


Booting the minimal image from QSPI
===================================

The U-Boot console is used to copy U-Boot (SPL and Proper), the minimal root filesystem image and the fitImage (which contains the kernel image and dtb file) into RAM and then write them to Flash. Copy the required files from ```<BUILD DIR>/tmp/deploy/images```` to your ````/tftpboot``` directory.


.. code-block:: shell

   cp tmp/deploy/images/adsp-sc598-som-ezkit/stage1-boot.ldr /tftpboot/
   cp tmp/deploy/images/adsp-sc598-som-ezkit/stage1-boot-unsigned.ldr /tftpboot/
   cp tmp/deploy/images/adsp-sc598-som-ezkit/stage2-boot.ldr /tftpboot/
   cp tmp/deploy/images/adsp-sc598-som-ezkit/stage2-boot-unsigned.ldr /tftpboot/
   cp tmp/deploy/images/adsp-sc598-som-ezkit/fitImage /tftpboot/
   cp tmp/deploy/images/adsp-sc598-som-ezkit/adsp-sc5xx-minimal-adsp-sc598-som-ezkit.jffs2 /tftpboot



If your network **supports** DHCP, run:

.. code-block:: shell

   => run update_spi

<details closed>
  <summary>If your network does NOT support DHCP</summary>
in the U-Boot console configure the board IP address and remove "run init*ethernet;" from the "start*update_spi" command.


.. code-block:: shell

   => setenv ipaddr <IPADDR>
   => edit start_update_spi
   => edit: <remove "run init_ethernet;" from here> sf probe ${sfdev}; sf erase 0 ${sfsize}; run update_spi_uboot; run update_spi_fit; run update_spi_rfs; sleep 3; saveenv


After editing ```start*update*spi````, proceed to running as ````update_spi```, as above.
</details>

You should see output similar to the following.

.. code-block:: shell

   => run update_spi
   PHY 0x00: OUI = 0x80028, Model = 0x23, Rev = 0x01, 100baseT, FDX
   Speed: 1000, full duplex
   BOOTP broadcast 1
   BOOTP broadcast 2
   BOOTP broadcast 3
   BOOTP broadcast 4
   BOOTP broadcast 5
   BOOTP broadcast 6
   BOOTP broadcast 7
   DHCP client bound to address 10.37.65.95 (7850 ms)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 4 KiB, total 64 MiB
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.65.102; our IP address is 10.37.65.95
   Filename 'stage1-boot-unsigned.ldr'.
   Load address: 0x90000000
   Loading: ######
            4.3 MiB/s
   done
   Bytes transferred = 80832 (13bc0 hex)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 4 KiB, total 64 MiB
   device 0 offset 0x0, size 0x13bc0
   0 bytes written, 80832 bytes skipped in 0.35s, speed 2178209 B/s
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.65.102; our IP address is 10.37.65.95
   Filename 'stage2-boot-unsigned.ldr'.
   Load address: 0x90000000
   Loading: #################################################################
            ###########
            6.1 MiB/s
   done
   Bytes transferred = 1113216 (10fc80 hex)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 4 KiB, total 64 MiB
   device 0 offset 0x40000, size 0x10fc80
   0 bytes written, 1113216 bytes skipped in 0.485s, speed 2335928 B/s
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.65.102; our IP address is 10.37.65.95
   Filename 'fitImage'.
   Load address: 0x90000000
   Loading: #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            #################################################################
            ################
            6.4 MiB/s
   done
   Bytes transferred = 8817402 (868afa hex)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 4 KiB, total 64 MiB
   device 0 offset 0x1a0000, size 0x868afa
   8817402 bytes written, 0 bytes skipped in 139.394s, speed 64833 B/s
   Speed: 1000, full duplex
   Using eth0 device
   TFTP from server 10.37.65.102; our IP address is 10.37.65.95
   Filename 'adsp-sc5xx-minimal-adsp-sc598-som-ezkit.jffs2'.
   Load address: 0x90000000
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
            #########
            6.6 MiB/s
   done
   Bytes transferred = 34471936 (20e0000 hex)
   SF: Detected is25lp512 with page size 256 Bytes, erase size 4 KiB, total 64 MiB
   device 0 offset 0x1020000, size 0x20e0000
   34451456 bytes written, 20480 bytes skipped in 525.324s, speed 67196 B/s
   Saving Environment to SPIFlash... Erasing SPI flash...Writing to SPI flash...done
   OK
   =>



The U-Boot image, root filesystem and Linux kernel are now stored in QSPI. Adjust the BOOT MODE selector to **position 1** and press the RESET button, the board should boot into Linux.




.. code-block:: shell

   U-Boot SPL 2023.04 (Dec 11 2023 - 16:04:11 +0000)
   ADI Boot Mode: 0x1 (QSPI Master)
   Trying to boot from BOOTROM
   NOTICE:  BL31: v2.6(release):v2.6-7-g85b6e2a3a-dirty
   NOTICE:  BL31: Built : 03:30:41, Sep 15 2023
   E/TC:0 0 tee_otp_get_hw_unique_key:49 HUK OTP is programmed with zeroes--please program a real HUK or enable CFG_ADI_AUTOGEN_HUK
   
   
   U-Boot 2023.04 (Dec 11 2023 - 16:04:11 +0000)
   
   CPU:   ADSP ADSP-SC598-0.0 (spi slave boot)
   Model: ADI sc598-som-ezkit
   DRAM:  224 MiB
   Core:  142 devices, 22 uclasses, devicetree: embed
   MMC:   mmc@310C7000: 0
   Loading Environment from SPIFlash... SF: Detected is25lp512 with page size 256 Bytes, erase size 4 KiB, total 64 MiB
   OK
   In:    serial@0x31003000
   Out:   serial@0x31003000
   Err:   serial@0x31003000
   Net:   eth0: eth0
   Hit any key to stop autoboot:  0
   SF: Detected is25lp512 with page size 256 Bytes, erase size 4 KiB, total 64 MiB
   device 0 offset 0x1a0000, size 0x868afa
   SF: 8817402 bytes @ 0x1a0000 Read: OK
   ## Loading kernel from FIT Image at 90000000 ...
      Using 'conf-1' configuration
      Verifying Hash Integrity ... OK
      Trying 'kernel-1' kernel subimage
        Description:  Linux kernel
        Type:         Kernel Image
        Compression:  gzip compressed
        Data Start:   0x900000e0
        Data Size:    6048922 Bytes = 5.8 MiB
        Architecture: AArch64
        OS:           Linux
        Load Address: 0x9a200000
        Entry Point:  0x9a200000
        Hash algo:    sha1
        Hash value:   2535fa3f1aa7f8eeb0f1705b8bfa51e4eb83ce90
        Sign algo:    sha1,rsa2048:dev
        Sign value:   unavailable
      Verifying Hash Integrity ... sha1+ sha1,rsa2048:dev- OK
   ## Loading ramdisk from FIT Image at 90000000 ...
      Using 'conf-1' configuration
      Verifying Hash Integrity ... OK
      Trying 'ramdisk-3' ramdisk subimage
        Description:  Initial Ram File System
        Type:         RAMDisk Image
        Compression:  uncompressed
        Data Start:   0x905cc0a4
        Data Size:    2738160 Bytes = 2.6 MiB
        Architecture: AArch64
        OS:           Linux
        Load Address: 0x9c000000
        Entry Point:  0x9c000000
        Hash algo:    sha1
        Hash value:   290cfca3369ae22d7db6ae85deabf02505d16a0a
        Sign algo:    sha1,rsa2048:dev
        Sign value:   unavailable
      Verifying Hash Integrity ... sha1+ sha1,rsa2048:dev- OK
      Loading ramdisk from 0x905cc0a4 to 0x9c000000
   ## Loading fdt from FIT Image at 90000000 ...
      Using 'conf-1' configuration
      Verifying Hash Integrity ... OK
      Trying 'fdt-2' fdt subimage
        Description:  Flattened Device Tree Blob
        Type:         Flat Device Tree
        Compression:  uncompressed
        Data Start:   0x905c4eb4
        Data Size:    28890 Bytes = 28.2 KiB
        Architecture: AArch64
        Load Address: 0x99000000
        Hash algo:    sha1
        Hash value:   fcc1a1eb9d09961853352904131e04e9dfabe459
        Sign algo:    sha1,rsa2048:dev
        Sign value:   unavailable
      Verifying Hash Integrity ... sha1+ sha1,rsa2048:dev- OK
      Loading fdt from 0x905c4eb4 to 0x99000000
      Booting using the fdt blob at 0x99000000
   Working FDT set to 99000000
      Uncompressing Kernel Image
      Using Device Tree in place at 0000000099000000, end 000000009900a0d9
   Working FDT set to 99000000
   
   Starting kernel ...
   
   [    0.000000] Booting Linux on physical CPU 0x0000000000 [0x412fd050]
   [    0.000000] Linux version 5.15.148-yocto-standard (oe-user@oe-host) (aarch64-adi_glibc-linux-gcc (GCC) 11.4.0, GNU ld (GNU Binutils) 2.38.20220708) #1 SMP PREEMPT Mon Feb 12 09:52:51 UTC 2024
   [    0.000000] Machine model: ADI 64-bit SC598 SOM EZ Kit
   
   ...
   
   
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
   
        ***************************************
        ************* PLEASE NOTE *************
        ***************************************
        * This is an evaluation system with   *
        * default username/password           *
        ***************************************
        ******* NOT FIT FOR PRODUCTION ********
        ***************************************
   
   adsp-sc598-som-ezkit login:

The username is **root** and the password is **adi**.

Program the secure boot key
===========================


Extract the Public key and copy it into the target
--------------------------------------------------


* cd into the directory where the keys were generated:


.. code-block:: shell

   cd /opt/adi-testkeys


* Dump the Key Pair to verify the content


.. code-block:: shell

   openssl asn1parse -in testkey.der -inform der -dump
       0:d=0  hl=2 l= 119 cons: SEQUENCE
       2:d=1  hl=2 l=   1 prim: INTEGER           :01
       5:d=1  hl=2 l=  32 prim: OCTET STRING
         0000 - 87 e8 15 3c a6 7a bf 82-2a 21 1f 27 6f 81 1a ae   ...<.z..*!.'o...
         0010 - b2 cd e6 7d 03 a7 ab 1e-39 26 c0 57 97 b3 e2 54   ...}....9&.W...T
      39:d=1  hl=2 l=  10 cons: cont [ 0 ]
      41:d=2  hl=2 l=   8 prim: OBJECT            :prime256v1
      51:d=1  hl=2 l=  68 cons: cont [ 1 ]
      53:d=2  hl=2 l=  66 prim: BIT STRING
         0000 - 00 04 5a 06 5d 03 19 19-fe b5 ee 87 ed 47 6b 4e   ..Z.]........GkN
         0010 - e6 c9 59 21 6f d0 11 fa-77 ae 6b 99 51 45 49 b9   ..Y!o...w.k.QEI.
         0020 - ec 15 91 9c c0 0e 2e 3b-9f 44 c1 4f f6 a8 f8 0e   .......;.D.O....
         0030 - a0 7d 6a d5 72 b4 5f 37-59 31 ef e8 bf 21 fd 76   .}j.r._7Y1...!.v
         0040 - 47 47                                             GG


* Extract the public key from the DER file with ``dd``


.. code-block:: shell

   dd if=testkey.der bs=1 count=64 skip=57 | xxd
   00000000: 5a06 5d03 1919 feb5 ee87 ed47 6b4e e6c9  Z.]........GkN..
   00000010: 5921 6fd0 11fa 77ae 6b99 5145 49b9 ec15  Y!o...w.k.QEI...
   00000020: 919c c00e 2e3b 9f44 c14f f6a8 f80e a07d  .....;.D.O.....}
   64+0 records in
   64+0 records out
   00000030: 6ad5 72b4 5f37 5931 efe8 bf21 fd76 4747  j.r._7Y1...!.vGG
   64 bytes copied, 0.000160752 s, 398 kB/s


* Save the public key into a file


.. code-block:: shell

   dd if=testkey.der bs=1 count=64 skip=57 > testkey_pub.bin
   64+0 records in
   64+0 records out
   64 bytes copied, 0.000347221 s, 184 kB/s


* Copy ``testkey_pub.bin`` file to the target with ``scp``, then flash it with ``adiotp-cli``


.. code-block:: shell

   scp testkey_pub.bin root@<TARGET_IP>:/tmp/testkey_pub.bin


Program the key into the OTP flash memory
-----------------------------------------


**Note:** This is a one-time, irreversible operation - the OTP (One-Time Programmable) Flash cannot be erased or rewritten.

```Terminal1: minicom```


.. code-block:: shell

   cd /tmp
   cat testkey_pub.bin | adiotp-cli -s 12


* Print the key with ``adiotp-cli`` to verify it was programmed successfully


.. code-block:: shell

   adiotp-cli 12 | xxd
   00000000: 5a06 5d03 1919 feb5 ee87 ed47 6b4e e6c9  Z.]........GkN..
   00000010: 5921 6fd0 11fa 77ae 6b99 5145 49b9 ec15  Y!o...w.k.QEI...
   00000020: 919c c00e 2e3b 9f44 c14f f6a8 f80e a07d  .....;.D.O.....}
   00000030: 6ad5 72b4 5f37 5931 efe8 bf21 fd76 4747  j.r._7Y1...!.vGG
   root@adsp-sc598-som-ezkit:~#


* Another method to verify key programmation successfully


.. code-block:: shell

   adiotp-cli 12 | sha256sum - /tmp/testkey_pub.bin
   7c4888b77901b12b8fdd3f69e0124727286fcdc14a85214befc1fb181f273c59  -
   7c4888b77901b12b8fdd3f69e0124727286fcdc14a85214befc1fb181f273c59  /tmp/testkey_pub.bin


Bravo, You have successfully programmed the secure boot key.

================
Using the System
================


Running OP-TEE applications
===========================


* Reboot your device then run ``xtest`` (**TEE sanity test suite**)

First of all, let's check the status of the ``tee-supplicant`` systemd service, then run the ``xtest``.


.. code-block:: shell

   root@adsp-sc598-som-ezkit:~# systemctl status tee-supplicant
   * tee-supplicant.service - TEE Supplicant
        Loaded: loaded (8;;file://adsp-sc598-som-ezkit/lib/systemd/system/tee-supplicant.service/lib/systemd/system/tee-supplicant.service8;;; enabled; vendor preset: enabled)
        Active: active (running) since Thu 2022-04-28 17:42:33 UTC; 3min 36s ago
      Main PID: 268 (tee-supplicant)
         Tasks: 1 (limit: 238)
        Memory: 544.0K
        CGroup: /system.slice/tee-supplicant.service
                `- 268 /usr/sbin/tee-supplicant
   
   Apr 28 17:42:33 adsp-sc598-som-ezkit systemd[1]: Started TEE Supplicant.
   root@adsp-sc598-som-ezkit:~#



.. code-block:: shell

   root@adsp-sc598-som-ezkit:~# xtest
   Run test suite with level=0
   
   TEE test application started over default TEE instance
   ######################################################
   #
   # regression
   #
   ######################################################
   
   * regression_1001 Core self tests
   E/LD:  init_elf:439 sys_open_ta_bin(d96a5b40-c3e5-21e3-8794-1002a5d5c61b)
   E/TC:? 0 ldelf_init_with_ldelf:130 ldelf failed with res: 0xffff0008
    - 1001 -   skip test, pseudo TA not found
     regression_1001 OK
   
   ...
   
   regression_8102 OK
   regression_8103 OK
   +-----------------------------------------------------
   26175 subtests of which 0 failed
   93 test cases of which 0 failed
   0 test cases were skipped
   TEE test application done!
   root@adsp-sc598-som-ezkit:~#


Using the SHARC Cores
=====================


Signing the SHARC firmware
--------------------------


First, copy the firmware files from ``/lib/firmware`` on the target system, to the host. Then, the following commands can be used to sign the firmware. Finally, transfer the signed files back to the target and place in ``/lib/firmware`` next to the original firmware.


.. code-block:: shell

   /opt/analog/cces/2.12.1/adi_signtool -proc ADSP-SC598 sign -type BLp -algo ecdsa256 -infile adi_adsp_core1_fw.ldr -outfile adi_adsp_core1_fw_signed.ldr -prikey /opt/adi-testkeys/testkey.der
   /opt/analog/cces/2.12.1/adi_signtool -proc ADSP-SC598 sign -type BLp -algo ecdsa256 -infile adi_adsp_core2_fw.ldr -outfile adi_adsp_core2_fw_signed.ldr -prikey /opt/adi-testkeys/testkey.der


Using the Signed Firmware
-------------------------


The following commands can then be used to load the signed firmware images.


.. code-block:: shell

   # Stop the SHARC cores
   sharc-cli -z 0
   sharc-cli -z 1
   
   # Halt the RPMsg driver
   rmmod adi_rpmsg
   
   # Load the firmware
   sharc-cli -l /lib/firmware/adi_adsp_core1_fw_signed.ldr 0
   sharc-cli -l /lib/firmware/adi_adsp_core2_fw_signed.ldr 1
   
   # Re-start the SHARC cores
   sharc-cli -g 0
   sharc-cli -g 1
   
   # Re-load the RPMsg driver
   modprobe adi_rpmsg


After running the ``modprobe`` command, you should see output similar to below:


.. code-block:: text

   [   77.043231] adi-rpmsg scb:core0-rpmsg@0x28240000: vrings in vdev-vring reserved-memory.
   [   77.059071] adi-rpmsg scb:core0-rpmsg@0x28240000: assigned reserved memory node vdev0buffer@20084000
   [   77.079176] adi-rpmsg scb:core0-rpmsg@0x28240000: msg buffers in memory-region.
   [   77.104359] adi-rpmsg scb:core1-rpmsg@0x28a40000: vrings in vdev-vring reserved-memory.
   [   77.125730] adi-rpmsg scb:core1-rpmsg@0x28a40000: assigned reserved memory node vdev0buffer@200A8000
   [   77.155792] adi-rpmsg scb:core1-rpmsg@0x28a40000: msg buffers in memory-region.
   [   77.183193] virtio_rpmsg_bus virtio0: creating channel sharc-echo addr 0x97
   [   77.190225] virtio_rpmsg_bus virtio0: creating channel sharc-echo-cap addr 0xa1
   [   77.199660] virtio_rpmsg_bus virtio0: rpmsg host is online
   [   77.207199] virtio_rpmsg_bus virtio1: creating channel sharc-echo addr 0x98
   [   77.214204] virtio_rpmsg_bus virtio1: creating channel sharc-echo-cap addr 0xa2
   [   77.221576] virtio_rpmsg_bus virtio1: rpmsg host is online


The SHARC cores are now up, and running the signed firmware images.

In order to communicate with the cores, we must first bind some RPMsg endpoints:


.. code-block:: shell

   rpmsg-bind-chardev -p virtio0.sharc-echo.-1. -n 1 -e 151 -s 50
   rpmsg-bind-chardev -p virtio0.sharc-echo-cap.-1. -n 1 -e 161 -s 61
   rpmsg-bind-chardev -p virtio1.sharc-echo.-1. -n 1 -e 152 -s 51
   rpmsg-bind-chardev -p virtio1.sharc-echo-cap.-1. -n 1 -e 162 -s 62


Communicating with the SHARC Cores
----------------------------------


Finally, we can  communicate with the SHARC cores. For example:


.. code-block:: shell

   $ echo hello | rpmsg-xmit -n 5 /dev/rpmsg0
   hello => echo from Core1
   $ echo hello | rpmsg-xmit -n 5 /dev/rpmsg1
   HELLO => capitalized echo from Core1
   $ echo hello | rpmsg-xmit -n 5 /dev/rpmsg2
   hello => echo from Core2
   $ echo hello | rpmsg-xmit -n 5 /dev/rpmsg3
   HELLO => capitalized echo from Core2


Mbed-TLS test suite
===================


Mbed-TLS provides a test suite for Yocto's ``ptest``.

Install ptest by adding the following to ``conf/local.conf``:


.. code-block:: text

   # Test Suite
   DISTRO_FEATURES:append = " ptest "
   IMAGE_INSTALL:append = " ptest-runner mbedtls-ptest "


This will build ``ptest`` and ``ptest-runner`` (used to run ``ptest`` test suites from the host system), and Mbed-TLS's test suite, ``mbedtls-ptest``.

To run the test suite, run the following from the target system:


.. code-block:: shell

   ptest-runner


You should see output similar to the following:

.. code-block:: text

   root@adsp-sc598-som-ezkit:/usr/lib/mbedtls/ptest/tests# ptest-runner
   START: ptest-runner
   2024-09-17T12:44
   BEGIN: /usr/lib/mbedtls/ptest
   PASS: test_suite_aes.cbc
   PASS: test_suite_aes.cfb
   PASS: test_suite_aes.ctr
   PASS: test_suite_aes.ecb
   PASS: test_suite_aes.ofb
   PASS: test_suite_aes.rest
   PASS: test_suite_aes.xts
   PASS: test_suite_alignment
   ...
   PASS: test_suite_x509parse
   PASS: test_suite_x509write
   DURATION: 556
   END: /usr/lib/mbedtls/ptest
   2024-09-17T12:53
   STOP: ptest-runner
   TOTAL: 1 FAIL: 0
