==============================
Yocto Linux Kernel development
==============================

ADSP Linux Kernel for release 3.1.2 based on Linux 5.15.168.

Minimal requirements to compile Kernel `https://docs.kernel.org/process/changes.html <https://docs.kernel.org/process/changes.html>`_

This steps applicable for all supported platforms and this is not complete guide for kernel development.
There is plenty resource on kernel development online:

`https://kernelnewbies.org/KernelBuild <https://kernelnewbies.org/KernelBuild>`_

`https://linux-kernel-labs.github.io/refs/heads/master/labs/arm_kernel_development.html <https://linux-kernel-labs.github.io/refs/heads/master/labs/arm_kernel_development.html>`_

`https://www.raspberrypi.com/documentation/computers/linux_kernel.html <https://www.raspberrypi.com/documentation/computers/linux_kernel.html>`_

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