Image Types
===========

Introduction
------------

The ADI Yocto meta layer ships several image recipes, each tailored to a
different use case. This page describes what each image contains, how they
differ, and how to create your own image type.

All image recipes live in ``meta-adi-adsp-sc5xx/recipes-adi/images/`` and the
shared behaviour is provided by the image classes in
``meta-adi-adsp-sc5xx/classes/``:

* ``adsp-sc5xx.bbclass`` — Common base image (packages, users, filesystem
  types, programming-image generation).
* ``adsp-sc5xx-minimal.bbclass`` — Minimal image base, inherited by the
  minimal image recipe.
* ``adsp-sc5xx-ramdisk.bbclass`` — Self-contained initramfs image.
* ``adsp-sc5xx-compatible.bbclass`` — Defines ``COMPATIBLE_MACHINE`` for all
  supported SoCs (SC57x, SC58x, SC594, SC598, SC846).
* ``adsp-image-flash.bbclass``, ``adsp-image-emmc.bbclass`` and
  ``adsp-image-nand.bbclass`` — The ``flash``, ``emmc`` and ``nand`` image
  types, which lay out the boot flash, the eMMC and the SPI NAND with genimage
  (see `Programming Images`_).
* ``adsp-genimage.bbclass`` — The genimage helper shared by those image types.

Available Images
----------------

.. list-table::
   :widths: 20 25 30 25
   :header-rows: 1

   * - Image
     - Recipe
     - Purpose
     - Filesystem types
   * - **Full**
     - ``adsp-sc5xx-full``
     - Development image with audio, networking and profiling tools
     - ``tar.xz``, ``ext4``, ``emmc``
   * - **Test**
     - ``adsp-sc5xx-test``
     - Full image plus on-target test suites (ptest, stress and benchmark tools)
     - ``tar.xz``, ``ext4``, ``emmc``
   * - **Minimal**
     - ``adsp-sc5xx-minimal``
     - Slimmed-down production rootfs
     - ``tar.xz``, ``erofs-lz4hc.ubi``, ``ext4``, ``flash``, ``emmc``,
       ``nand``
   * - **Tiny**
     - ``adsp-sc5xx-tiny``
     - Smallest image for boards with small SPI flashes, like the 16MB flash on the sc573
     - ``tar.xz``, ``erofs-lz4hc.ubi``, ``ext4``, ``flash``
   * - **Ramdisk**
     - ``adsp-sc5xx-ramdisk``
     - Initramfs used as the boot ramdisk
     - ``cpio.xz``, ``cpio.gz``

``emmc`` is only built on machines with the ``emmc`` machine feature (the
ADSP-SC598 and ADSP-SC846), and ``nand`` on machines with the ``nand`` machine
feature (the ADSP-SC846).

Base Image (``adsp-sc5xx``)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``adsp-sc5xx`` class is the foundation for the full, minimal and test
images. It provides a Linux userspace with a common set of packages:

* **Core boot / base**: ``packagegroup-core-boot``, ``packagegroup-base``,
  ``busybox-watchdog-init``, ``util-linux``.
* **Connectivity / shell**: ``openssh``, ``iproute2``, ``iproute2-tc``,
  ``ncurses``.
* **Audio + inter-core comms (ICC)**: ``alsa-utils``, ``rpmsg-utils`` and the
  SHARC audio binaries selected by the audio ``DISTRO_FEATURES``.
* **Crypto**: ``openssl``, ``openssl-bin``, ``cryptodev-linux``,
  ``cryptodev-module``.
* **Flash / storage tools**: ``mtd-utils``, ``mtd-utils-ubifs``, ``e2fsprogs``.
* **GPIO / SPI**: ``libgpiod``, ``libgpiod-tools``, ``spidev-test``,
  ``spitools``.


Full Image
~~~~~~~~~~~

``adsp-sc5xx-full`` inherits the base image and layers on tooling for
development and benchmarking. In addition to everything in the base image it
adds:

* **Networking / benchmarking**: ``iperf3``, ``netperf``, ``linuxptp``.
* **Tracing / profiling**: ``uftrace``, ``ltrace``, ``strace``,
  ``cpufrequtils``.
* **Audio / media**: ``alsa-lib``, ``dbus``, ``libopus``, ``opus-tools``,
  ``linux-firmware-adau1761``.
* **Test suites / misc**: ``ltp``, ``python3``, ``linux-firmware-rtl8192su``.


Test Image
~~~~~~~~~~

``adsp-sc5xx-test`` requires the full image and adds
``packagegroup-adsp-sc5xx-tests``, which installs test suites that can be run
directly on the board:

* **ptest**: ``ptest-runner`` and the ``busybox``, ``libseccomp``, ``openssh``,
  ``rt-tests`` and ``util-linux`` ptest packages, plus ``bzip2`` for the
  BusyBox ``bunzip2`` / ``bzcat`` tests, and the ADI ``crc-tests-ptest``
  package. On the ADSP-SC598 SOM EZ-KIT the image also installs the ADI
  ``crypto-tests-ptest`` package.
* **Stress, memory and storage**: ``stress-ng``, ``memtester``, ``fio``.
* **Real-time latency**: ``rt-tests`` (``cyclictest``).
* **Entropy**: ``rng-tools`` (``rngtest``).

When the image is built with the ``adi-security`` distro, the security layer
also adds ``packagegroup-adsp-sc5xx-security-tests`` (ADSP-SC598 only), which
installs:

* ``optee-test-ptest`` — the OP-TEE ``xtest`` suite.
* ``mbedtls-ptest`` — the Mbed TLS library test suites.
* ``tee-smoke-ptest`` — Linux-visible checks of the TF-A (BL31) and OP-TEE
  stack.

The test image uses the same kernel configuration as the other images. The
``ptest`` ``DISTRO_FEATURE`` must be enabled, which it is by default for the
ADI distros; without it the image is skipped. See :doc:`Running-Tests` for how
to run each suite and its options.


Minimal Image
~~~~~~~~~~~~~~

``adsp-sc5xx-minimal`` inherits the base image and then runs a rootfs cleanup
step that removes items not needed on a deployed device to reduce the rootfs
footprint. This image is a good starting point for creating your own production
rootfs.

Tiny Image
~~~~~~~~~~

``adsp-sc5xx-tiny`` is a standalone image built directly from ``core-image``
for boards with limited SPI flash space. It installs minimal packages, produces
a read-only EROFS filesystem for the SPI flash, and aggressively strips the
rootfs to keep the file system size down. It is the smallest bootable image the layer produces.
This image is only intended for small SPI flashes; larger boot media should use
the minimal or full images.

Ramdisk Image
~~~~~~~~~~~~~~

``adsp-sc5xx-ramdisk`` builds an initramfs (RAM disk) rather than an on-disk
rootfs. It uses ``nopackages`` and installs the selected SHARC audio binaries.
The base image references it through ``INITRD_NAME`` so it is used as the boot
ramdisk.

Programming Images
~~~~~~~~~~~~~~~~~~

Each image copies the files that program a board into
``programming-images/<image>/`` in the deploy directory:

.. list-table::
   :widths: 20 80
   :header-rows: 1

   * - File
     - Contents
   * - ``u-boot-spl.ldr``, ``u-boot.ldr``
     - U-Boot SPL and U-Boot, for the ``u-boot-spl`` and ``u-boot`` partitions
       of the boot flash
   * - ``fitImage``
     - FIT image with the kernel, the device tree and the ramdisk, for the
       ``kernel`` partition of the boot flash
   * - ``rootfs.ubi``
     - Read-only, LZ4HC-compressed EROFS root filesystem in a static UBI
       volume named ``rootfs``, for the ``rootfs`` partition of the boot flash.
       Write it with ``ubiformat``. U-Boot's ``spiboot`` attaches UBI to the
       partition by name (``ubi.mtd=rootfs``) and mounts the volume read-only
       from its ``ubiblock`` device, so the root doesn't depend on the MTD
       numbers.
   * - ``rootfs.ext4``
     - ext4 root filesystem for the eMMC, SD card and USB
   * - ``flash.img``
     - The whole boot flash: ``u-boot-spl.ldr``, ``u-boot.ldr``, ``fitImage``
       and ``rootfs.ubi`` at the offsets of the boot flash partitions in the
       machine's device tree, laid out by ``files/genimage/flash.cfg``
   * - ``emmc.img``
     - ADSP-SC598 and ADSP-SC846 only: the eMMC, with ``u-boot-spl.ldr`` and
       ``u-boot.ldr`` at 17K and 256K, ``fitImage`` on the ext4 partition 1
       (``mmcboot``) and ``rootfs.ext4`` as partition 2 (``mmcargs``), laid out
       by ``files/genimage/emmc.cfg``
   * - ``nand.img``
     - ADSP-SC846 only: a UBI image for the SPI NAND, with static volumes for
       ``u-boot-spl.ldr``, ``u-boot.ldr``, ``fitImage`` and the EROFS root
       filesystem, and writable UBIFS ``overlay`` and ``data`` volumes, laid
       out by ``files/genimage/nand.cfg``. Write it with ``ubiformat``.
       U-Boot's ``nandboot`` attaches UBI to the ``xspi1-data`` partition by
       name and mounts the ``rootfs`` volume from its ``ubiblock`` device.

The boot flash partition sizes come from the ``ADSP_SPI_*`` variables in the
machine configuration, which follow the partitions in the device tree. The full
and test images, and the images on the ADSP-SC573 other than the tiny image,
are too large for the boot flash ``rootfs`` partition, so they don't build
``rootfs.ubi``, ``flash.img`` or ``nand.img``.

Building an Image
-----------------

From the workspace created in the Getting Started guide, open a kas shell
and build any image by name with ``bitbake``:

.. shell::

   $. .venv/bin/activate
   $KAS_MACHINE=adsp-sc598-som-ezkit kas shell config/kas/adsp-sc5xx.yml
   $bitbake adsp-sc5xx-full

The resulting rootfs and programming images are written under
``build/tmp/deploy/images/<machine>/``.

Creating a New Image Type
-------------------------

To add your own image type to the layer, create a new recipe under
``recipes-adi/images/`` and inherit whichever base best matches your needs. The
example below defines an ``adsp-sc5xx-myimage`` image that starts from the
minimal image and adds a couple of extra packages.

Create ``meta-adi-adsp-sc5xx/recipes-adi/images/adsp-sc5xx-myimage.bb``:

.. code-block:: bitbake

   inherit adsp-sc5xx-minimal

   SUMMARY = "Custom product image for Analog Devices ADSP-SC5xx boards"
   LICENSE = "MIT"

   IMAGE_INSTALL += " \
       my-application \
       my-application2 \
       nano \
       i2c-tools \
   "

Choose the base to inherit according to your goal:

* ``inherit adsp-sc5xx`` — Full-featured base with the complete default package
  set.
* ``inherit adsp-sc5xx-minimal`` — The base plus rootfs size reduction.
* ``inherit adsp-sc5xx-ramdisk`` — A RAM disk / initramfs image.
* ``inherit core-image adsp-sc5xx-compatible adsp-fit-generation`` — Build an
  image from scratch

.. note::
   ``adsp-sc5xx-compatible`` provides ``COMPATIBLE_MACHINE`` for the supported
   SoCs. If you inherit one of the ``adsp-sc5xx*`` bases you get this
   automatically; if you build an image from ``core-image`` directly, inherit
   ``adsp-sc5xx-compatible`` so the image is restricted to ADI machines.

Customise the image with the standard Yocto variables:

* ``IMAGE_INSTALL`` — Add packages to the rootfs (use ``+=`` to extend an
  inherited list).
* ``IMAGE_FSTYPES`` — Select output filesystem types (e.g. ``ext4``,
  ``erofs-lz4hc.ubi``, ``cpio.gz``).
* ``IMAGE_FEATURES`` — Enable Yocto image features such as ``debug-tweaks`` or
  ``tools-debug``.

.. shell::

   $bitbake adsp-sc5xx-myimage

.. note::
   If your customisation is product-specific and extends beyond just
   adding/removing packages, it is recommended to keep the new image recipe in
   your own layer instead of the public ADI reference layer. See
   :doc:`Custom-Meta-Layer` for how to create and register a custom layer.
