==============================
Yocto Linux Kernel development
==============================

Introduction
============

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

ADSP Linux Kernel
=================

Obtain kernel sourcecode

.. code-block:: text

   git clone https://github.com/analogdevicesinc/lnxdsp-linux.git

Build
=====

default configuration for SC598

.. code-block:: text

   ARCH=arm64 CROSS_COMPILE=aarch64-linux-gnu-  make sc598-som-ezkit_defconfig

default configuration for SC589-mini

.. code-block:: text

   ARCH=arm CROSS_COMPILE=arm-linux-gnueabi- make sc589-mini_defconfig

compile kernel device tree and kernel image for SC598

.. code-block:: text

   ARCH=arm64 CROSS_COMPILE=aarch64-linux-gnu- make -j`nproc` Image.gz modules dtbs

compile kernel device tree and kernel image for SC589

.. code-block:: text

   ARCH=arm CROSS_COMPILE=arm-linux-gnueabi- make -j`nproc` zImage modules dtbs

Rebuild fitImage
================

Currently, the default boot format used is fitImage. Here is shared example
how to rebuild fitImage with newly compiled kernel.

.. code-block:: text

   mkdir new_fit_image
   cd new_fit_image
   cp <buildir>/tmp/deploy/images/adsp-sc598-som-ezkit/fit-image.its ./
   cp <buildir>/tmp/deploy/images/adsp-sc598-som-ezkit/adsp-sc5xx-ramdisk-adsp-sc598-som-ezkit.cpio.gz ./
   cp <kernel_buildir>/arch/arm64/boot/dts/adi/sc598-som-ezkit.dtb ./
   cp <kernel_buildir>/arch/arm64/boot/Image.gz ./
   mkimage -f fit-image.its fitImage

boot fitImage with your preferred boot method.

Notes
=====

* kernel modules compiled need to be copied to filesystem in use, if NFS boot is used copy NFS boot directory
* after changes/modifications to kernel is done create a patch and add to your yocto layer or create custom repository and point recipe to that
* apply required patches from meta-adi Yocto layer kernel recipe