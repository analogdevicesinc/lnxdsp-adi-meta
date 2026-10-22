Linux Kernel Development
========================

Introduction
------------

This guide helps you develop and customise the Linux kernel for ADSP-SC5xx processors. Whether you're debugging driver issues, adding new hardware support, or optimizing performance, this page walks you through compiling and rebuilding the kernel outside of the full Yocto build system.

While Yocto automates the entire build process, direct kernel development allows for faster iteration during development. You can quickly compile changes, test them on your hardware, and integrate them back into your Yocto layer once they're stable.

**What you'll learn:**

* How to obtain and compile the ADSP Linux kernel source
* Building kernel images and device trees for different boards
* Creating fitImage boot files with your custom kernel
* Integrating your changes back into Yocto

Prerequisites
-------------

Before starting kernel development, ensure you have:

* Cross-compilation toolchain installed (from the SDK or your host package manager)
* Basic understanding of Linux kernel development concepts
* Minimal build requirements: `kernel.org build requirements <https://docs.kernel.org/process/changes.html>`_

**Additional kernel development resources:**

* `Kernel Newbies - Kernel Build <https://kernelnewbies.org/KernelBuild>`_
* `ARM Kernel Development Labs <https://linux-kernel-labs.github.io/refs/heads/master/labs/arm_kernel_development.html>`_
* `Raspberry Pi Kernel Documentation <https://www.raspberrypi.com/documentation/computers/linux_kernel.html>`_

Obtaining the Kernel Source
----------------------------

Clone the ADSP Linux kernel repository:

.. code-block:: shell

   git clone https://github.com/analogdevicesinc/lnxdsp-linux.git
   cd lnxdsp-linux

Building the Kernel
-------------------

Configure for Your Board
~~~~~~~~~~~~~~~~~~~~~~~~

Load the default configuration for your target board.

**For ADSP-SC598 (ARM64):**

.. code-block:: shell

   ARCH=arm64 CROSS_COMPILE=aarch64-linux-gnu- make sc598-som-ezkit_defconfig

**For ADSP-SC589-MINI (ARM):**

.. code-block:: shell

   ARCH=arm CROSS_COMPILE=arm-linux-gnueabi- make sc589-mini_defconfig

Compile Kernel, Modules, and Device Trees
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**For ADSP-SC598 (ARM64):**

.. code-block:: shell

   ARCH=arm64 CROSS_COMPILE=aarch64-linux-gnu- make -j`nproc` Image.gz modules dtbs

**For ADSP-SC589 (ARM):**

.. code-block:: shell

   ARCH=arm CROSS_COMPILE=arm-linux-gnueabi- make -j`nproc` zImage modules dtbs

Creating a fitImage
-------------------

The default boot format used is fitImage. Here's how to rebuild a fitImage with your newly compiled kernel:

.. code-block:: shell

   mkdir new_fit_image
   cd new_fit_image
   cp <builddir>/tmp/deploy/images/adsp-sc598-som-ezkit/fit-image.its ./
   cp <builddir>/tmp/deploy/images/adsp-sc598-som-ezkit/adsp-sc5xx-ramdisk-adsp-sc598-som-ezkit.cpio.gz ./
   cp <kernel_builddir>/arch/arm64/boot/dts/adi/sc598-som-ezkit.dtb ./
   cp <kernel_builddir>/arch/arm64/boot/Image.gz ./
   mkimage -f fit-image.its fitImage

Boot the fitImage using your preferred boot method.

Integrating Changes
-------------------

Deploying Kernel Modules
~~~~~~~~~~~~~~~~~~~~~~~~~

Kernel modules must be copied to the filesystem in use:

* For NFS boot: Copy modules to the NFS boot directory
* For other boot methods: Update the root filesystem with the compiled modules

Integrating into Yocto
~~~~~~~~~~~~~~~~~~~~~~

After developing and testing your kernel changes:

1. Create a patch file with your modifications
2. Add the patch to your Yocto layer
3. Alternatively, create a custom repository and point the kernel recipe to it
4. Apply any required patches from the meta-adi Yocto layer kernel recipe

.. note::

   For persistent changes across builds, integrate your modifications into your Yocto layer rather than maintaining manual builds.
