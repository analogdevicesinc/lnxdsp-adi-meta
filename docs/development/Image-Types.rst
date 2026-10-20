Image Types
===========

Introduction
------------

The ADI Yocto meta layer ships several image recipes, each tailored to a different
use case. This page describes what each image contains, how they differ, and how to
create your own image type.

All image recipes live in
``meta-adi-adsp-sc5xx/recipes-adi/images/`` and the shared behaviour is provided by the
image classes in ``meta-adi-adsp-sc5xx/classes/``:

* ``adsp-sc5xx.bbclass`` — Common base image (packages, users, filesystem types,
  programming-image generation).
* ``adsp-sc5xx-minimal.bbclass`` — Minimal image that connects the ext4 image to WIC image generation.
* ``adsp-sc5xx-ramdisk.bbclass`` — Self-contained initramfs image.
* ``adsp-sc5xx-compatible.bbclass`` — Defines ``COMPATIBLE_MACHINE`` for all supported
  SoCs (SC57x, SC58x, SC594, SC598, SC846).

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
     - ``tar.xz``, ``ext4``, ``wic``
   * - **Test**
     - ``adsp-sc5xx-test``
     - Full image plus on-target test suites (ptest, stress and benchmark tools)
     - ``tar.xz``, ``ext4``, ``wic``
   * - **Minimal**
     - ``adsp-sc5xx-minimal``
     - Slimmed-down production rootfs
     - ``tar.xz``, ``ubi``, ``ext4``, ``wic``
   * - **Tiny**
     - ``adsp-sc5xx-tiny``
     - Smallest image for boards with small SPI flashes, like the 16MB flash on the sc573
     - ``ubi``
   * - **Ramdisk**
     - ``adsp-sc5xx-ramdisk``
     - Initramfs used as the boot ramdisk
     - ``cpio.xz``, ``cpio.gz``

Base Image (``adsp-sc5xx``)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``adsp-sc5xx`` class is the foundation for the full, minimal and test images. It provides a
Linux userspace with a common set of packages:

* **Core boot / base**: ``packagegroup-core-boot``, ``packagegroup-base``,
  ``busybox-watchdog-init``, ``util-linux``.
* **Connectivity / shell**: ``openssh``, ``iproute2``, ``iproute2-tc``, ``ncurses``.
* **Audio + inter-core comms (ICC)**: ``alsa-utils``, ``rpmsg-utils`` and the SHARC audio
  binaries selected by the audio ``DISTRO_FEATURES``.
* **Crypto**: ``openssl``, ``openssl-bin``, ``cryptodev-linux``, ``cryptodev-module``,
  ``crypto-tests``, ``crc-tests``.
* **Flash / storage tools**: ``mtd-utils``, ``mtd-utils-ubifs``, ``e2fsprogs``.
* **GPIO / SPI**: ``libgpiod``, ``libgpiod-tools``, ``spidev-test``, ``spitools``.


Full Image
~~~~~~~~~~~

``adsp-sc5xx-full`` inherits the base image and layers on tooling for development and
benchmarking. In addition to everything in the base image it adds:

* **Networking / benchmarking**: ``iperf3``, ``netperf``, ``linuxptp``.
* **Tracing / profiling**: ``uftrace``, ``ltrace``, ``strace``, ``cpufrequtils``.
* **Audio / media**: ``alsa-lib``, ``dbus``, ``libopus``, ``opus-tools``,
  ``linux-firmware-adau1761``.
* **Misc**: ``python3``, ``linux-firmware-rtl8192su``.


Test Image
~~~~~~~~~~

``adsp-sc5xx-test`` requires the full image and adds ``packagegroup-adsp-sc5xx-tests``, which
installs test suites that can be run directly on the board:

* **ptest**: ``ptest-runner`` and the ``busybox``, ``libseccomp``, ``openssh``, ``rt-tests``
  and ``util-linux`` ptest packages, plus ``bzip2`` for the BusyBox ``bunzip2`` / ``bzcat``
  tests. On the ADSP-SC598 the image also installs the ADI
  ``crypto-tests-ptest`` and ``crc-tests-ptest`` packages.
* **Stress, memory and storage**: ``stress-ng``, ``memtester``, ``fio``.
* **Real-time latency**: ``rt-tests`` (``cyclictest``).
* **Entropy**: ``rng-tools`` (``rngtest``).

When the image is built with the ``adi-security`` distro, the security layer also adds
``packagegroup-adsp-sc5xx-security-tests`` (ADSP-SC598 only), which installs:

* ``optee-test-ptest`` — the OP-TEE ``xtest`` suite.
* ``mbedtls-ptest`` — the Mbed TLS library test suites.
* ``tee-smoke-ptest`` — Linux-visible checks of the TF-A (BL31) and OP-TEE stack.

The test image uses the same kernel configuration as the other images. The ``ptest``
``DISTRO_FEATURE`` must be enabled, which it is by default for the ADI distros; without it
the image is skipped.
See :doc:`Running-Tests` for how to run each suite and its options.


Minimal Image
~~~~~~~~~~~~~~

``adsp-sc5xx-minimal`` inherits the base image and then runs a rootfs cleanup step
that removes items not needed on a deployed device to reduce the rootfs footprint.
This image is a good starting point for creating your own production rootfs.

Tiny Image
~~~~~~~~~~

``adsp-sc5xx-tiny`` is a standalone image built directly from ``core-image`` for boards with
limited SPI flash space. It installs minimal packages, produces a single ``ubi`` filesystem,
and aggressively strips the rootfs to keep the file system size down. It is the smallest
bootable image the layer produces. This image is only intended for small SPI flashes; larger
boot media should use the minimal or full images.

Ramdisk Image
~~~~~~~~~~~~~~

``adsp-sc5xx-ramdisk`` builds an initramfs (RAM disk) rather than an on-disk rootfs. It uses
``nopackages`` and installs the selected SHARC audio binaries. The base image references it
through ``INITRD_NAME`` so it is used as the boot ramdisk.

Building an Image
-----------------

From the workspace created in the Getting Started guide, open a kas shell
and build any image by name with ``bitbake``:

.. code-block:: shell

   . .venv/bin/activate
   KAS_MACHINE=adsp-sc598-som-ezkit kas shell config/kas/adsp-sc5xx.yml
   bitbake adsp-sc5xx-full

The resulting rootfs and programming images are written under
``build/tmp/deploy/images/<machine>/``.

Creating a New Image Type
-------------------------

To add your own image type to the layer, create a new recipe under
``recipes-adi/images/`` and inherit whichever base best matches your needs. The example
below defines an ``adsp-sc5xx-myimage`` image that starts from the minimal image and adds
a couple of extra packages.

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

* ``inherit adsp-sc5xx`` — Full-featured base with the complete default package set.
* ``inherit adsp-sc5xx-minimal`` — The base plus rootfs size reduction.
* ``inherit adsp-sc5xx-ramdisk`` — A RAM disk / initramfs image.
* ``inherit core-image adsp-sc5xx-compatible adsp-fit-generation`` — Build an image from scratch

.. note::
   ``adsp-sc5xx-compatible`` provides ``COMPATIBLE_MACHINE`` for the supported SoCs. If you
   inherit one of the ``adsp-sc5xx*`` bases you get this automatically; if you build an
   image from ``core-image`` directly, inherit ``adsp-sc5xx-compatible`` so the image is
   restricted to ADI machines.

Customise the image with the standard Yocto variables:

* ``IMAGE_INSTALL`` — Add packages to the rootfs (use ``+=`` to extend an inherited list).
* ``IMAGE_FSTYPES`` — Select output filesystem types (e.g. ``ext4``, ``ubi``, ``wic``,
  ``cpio.gz``).
* ``IMAGE_FEATURES`` — Enable Yocto image features such as ``debug-tweaks`` or
  ``tools-debug``.

.. code-block:: shell

   bitbake adsp-sc5xx-myimage

.. note::
   If your customisation is product-specific and extends beyond just adding/removing packages,
   it is recommended to keep the new image recipe in your own layer instead of the public ADI
   reference layer. See :doc:`Custom-Meta-Layer` for how to create and register a custom layer.
