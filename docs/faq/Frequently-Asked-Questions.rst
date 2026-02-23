===========================
Frequently Asked Questions
===========================

Introduction
============

This page answers common questions about developing with Linux for ADSP-SC5xx processors. Whether you're customizing your filesystem, debugging SHARC applications, or configuring peripherals, you'll find practical solutions and best practices here.

The FAQ is organized by topic to help you quickly find relevant information. For additional support, visit the `EngineerZone forum <https://ez.analog.com/linux-software-drivers>`_ or check the `GitHub issues <https://github.com/analogdevicesinc/lnxdsp-adi-meta/issues>`_.

**Quick Navigation:**

* `Yocto and Build System`_
* `Debugging SHARC Applications`_
* `Custom Development Repositories`_
* `Hardware Configuration`_

----

Yocto and Build System
======================

How do I add packages to my own Linux filesystem?
--------------------------------------------------

Let's try, for example, to add the package ``ethtool`` to your own target adsp-custom-ramdisk image.

Find the Yocto Project recipe
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

You can add your own packages instead of ``ethtool``. The first step is to find the Yocto Project recipe for your desired package. The best way to locate recipes is through the `OpenEmbedded Layer Index <https://layers.openembedded.org/layerindex/branch/master/recipes/>`_.

The example below demonstrates how to search for packages on the OpenEmbedded Layer Index:

.. image:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/7bfec7fc-df8f-4bad-819c-a7484dbb2075
   :width: 600
   :alt: OpenEmbedded Layer Index search example

Add the package to the filesystem
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**Method 1: Using local.conf (Recommended)**

After finding the recipe name, add it to your image by appending this line to ``conf/local.conf``:

.. code-block:: shell

   IMAGE_INSTALL:append = " ethtool"

.. note::
   Always include a space before the package name when using ``:append`` to avoid concatenation with the previous entry.

**Method 2: Using a custom recipe**

Alternatively, add the package directly to your custom recipe file. For example, this patch adds ``ethtool`` to the ``adsp-custom-ramdisk`` filesystem:

.. code-block:: diff

   diff --git a/meta-custom/recipes-custom/images/adsp-custom-ramdisk.bb b/meta-custom/recipes-custom/images/adsp-custom-ramdisk.bb
   index a98b1f9..e070e6f 100644
   --- a/meta-custom/recipes-custom/images/adsp-custom-ramdisk.bb
   +++ b/meta-custom/recipes-custom/images/adsp-custom-ramdisk.bb
   @@ -6,6 +6,8 @@ IMAGE_INSTALL = " \
        packagegroup-core-boot \
        linux-firmware-fastboot \
        fastboot-listener \
   +    ethtool \
   "
   DISTRO_FEATURES = " ram"

Build the target images
~~~~~~~~~~~~~~~~~~~~~~~~

Build your image to include the new package:

.. code-block:: shell

   bitbake adsp-custom-ramdisk

The package will be deployed into the Linux filesystem during the build process.

**See also:** :doc:`Yocto Linux Kernel Development <../development/Yocto-Linux-Kernel-development>`

----

Debugging SHARC Applications
=============================

How do I debug a SHARC application whilst running Linux on the ARM core?
-------------------------------------------------------------------------

When debugging SHARC applications using CrossCore Embedded Studio (CCES) while Linux runs on the ARM core, you must configure the debug session carefully to avoid interfering with Linux execution.

Since the ARM core boots first and starts Linux, the system is already running when you connect the debugger. The following settings are essential for safe SHARC debugging.

Overview: Required debug session settings
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To debug SHARC cores safely while Linux runs on ARM:

1. **Do not load preloads or applications to the ARM core** - Prevents memory corruption
2. **Disable processor reset** - Keeps Linux running
3. **Disable semihosting** - Avoids interference with Linux system calls

Step 1: Do not load any preloads or applications to the ARM core
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**Problem:** By default, CCES attempts to load applications onto the ARM core, which overwrites the L2 and L3 memory used by Linux.

**Solution:** When creating the debug session:

* Remove any preload or initcode binaries for the ARM core
* Ensure no application is loaded to the ARM core
* This preserves the memory regions reserved for Linux

.. note::
   This assumes you're using the default memory configuration or have correctly partitioned memory between cores. See :doc:`Configuring System Memory <../development/Configuring-System-Memory-When-Using-Linux-and-SHARC-Applications>`.

Step 2: Disable processor reset on reload
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**Problem:** CCES resets the entire processor when starting a debug session, which erases Linux from memory.

**Solution:** In the debug session settings:

* Uncheck **"Reset on reload"**
* This allows the debug session to attach without resetting the system

Step 3: Disable semihosting
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**Problem:** CCES uses ARM Supervisor Call (SVC) instructions for host communication. Linux also uses these instructions for system calls, causing severe performance degradation or crashes when semihosting is enabled.

**Solution:** In the debug session settings:

* Uncheck **"Use semihosting"**
* This prevents the emulator from halting on every SVC instruction

**Result:** Linux continues running normally while you debug SHARC applications.

----

Custom Development Repositories
================================

How do I develop Linux for ADSP-SC5xx with my own repositories?
----------------------------------------------------------------

This guide explains how to configure your Yocto build to use custom Git repositories for development, allowing you to work with your own forks or private repositories.

Prerequisites
~~~~~~~~~~~~~

Ensure your host PC is properly configured: :doc:`Setting Up Your Host PC <../getting-started/Setting-Up-Your-Host-PC>`.

Step 1: Download source code
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: shell

   $ mkdir ~/linux-dsp-own-repos
   $ cd ~/linux-dsp-own-repos
   $ mkdir bin
   $ curl http://commondatastorage.googleapis.com/git-repo-downloads/repo > ./bin/repo
   $ chmod a+x ./bin/repo
   $ ./bin/repo init \
    -u https://github.com/analogdevicesinc/lnxdsp-repo-manifest.git \
    -b main \
    -m release-5.0.1.xml

.. note::
   Replace ``release-5.0.1.xml`` with your desired release version.

Step 2: Configure manifest to use your repositories
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Modify the repo manifest to point to your custom repositories for ``lnxdsp-adi-meta`` and ``lnxdsp-scripts``:

.. code-block:: shell

   $ cd ~/linux-dsp-own-repos/.repo/manifests/

Apply the below modifications into the ``lnxdsp-repo-manifest``

.. code-block:: diff

   diff --git a/default.xml b/default.xml
   index 244bf04..45abdf8 100644
   --- a/default.xml
   +++ b/default.xml
   @@ -5,12 +5,12 @@

      <remote fetch="https://git.yoctoproject.org/git" name="yocto"/>
      <remote fetch="https://github.com/openembedded" name="oe"/>
   -  <remote fetch="https://github.com/analogdevicesinc" name="adigithub"/>
   +  <remote fetch="$YOUR_REPO_PATH" name="customrepo"/>

      <project remote="yocto" revision="..." name="poky" path="sources/poky"/>
      <project remote="oe" revision="..." name="meta-openembedded" path="sources/meta-openembedded"/>
   -  <project remote="adigithub" revision="main" name="lnxdsp-adi-meta" path="sources/meta-adi"/>
   -  <project remote="adigithub" revision="main" name="lnxdsp-scripts" path="sources">
   +  <project remote="customrepo" revision="main" name="lnxdsp-adi-meta" path="sources/meta-adi"/>
   +  <project remote="customrepo" revision="main" name="lnxdsp-scripts" path="sources">
   	  <linkfile dest="setup-environment" src="setup-environment"/>
      </project>

Replace ``$YOUR_REPO_PATH`` with your Git server URL (e.g., ``https://github.com/yourorg`` or ``git://your-server.com``).

Sync the repositories:

.. code-block:: shell

   $ ./bin/repo sync

Step 3: Configure U-Boot and Linux kernel repositories
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Point U-Boot and Linux kernel recipes to your custom repositories by modifying ``conf/local.conf``:

.. code-block:: diff

.. code-block:: shell

   # Add to conf/local.conf
   UBOOT_GIT_URI = "git://$YOUR_REPO_PATH/u-boot.git"
   UBOOT_BRANCH = "main"
   KERNEL_GIT_URI = "git://$YOUR_REPO_PATH/lnxdsp-linux.git"
   KERNEL_BRANCH = "main"

Replace ``$YOUR_REPO_PATH`` with your repository locations and adjust branch names as needed.

Step 4: Build with custom repositories
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Configure your build environment and start building:

.. code-block:: shell

.. code-block:: shell

   $ cd ~/linux-dsp-own-repos/
   $ source setup-environment -m adsp-sc598-som-ezkit

   # Build your target
   $ bitbake adsp-sc5xx-minimal

**Common build targets:**

* ``u-boot-adi`` - U-Boot bootloader
* ``linux-adi`` - Linux kernel
* ``adsp-sc5xx-minimal`` - Minimal root filesystem image
* ``adsp-sc5xx-full`` - Full-featured root filesystem image

**Benefits of using custom repositories:**

* Maintain proprietary modifications
* Implement custom versioning and branching strategies
* Enable CI/CD integration with your development workflow
* Control access and review processes

----

Hardware Configuration
======================

How do I allocate a peripheral to the SHARC?
---------------------------------------------

Default peripheral allocation between SHARC and ARM
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

By default, all peripherals are allocated to the ARM core. The ARM core, as the booting processor, manages **pinmux** (pin multiplexing) configuration for all peripherals. Only one core should configure the pinmux to avoid conflicts.

Peripheral allocation is controlled through Linux device tree files, located in the kernel source at ``arch/arm/boot/dts`` (ARM) or ``arch/arm64/boot/dts/adi`` (ARM64). Each platform has:

* A family-level device tree (e.g., ``sc59x.dtsi``)
* A board-specific device tree (e.g., ``sc594-som-ezkit.dts``)

**Example: Linkport0 device tree entries**

Family-level device tree (``sc59x.dtsi``):

.. code-block:: text

.. code-block:: dts

   lp0: linkport@0 {
       compatible = "linkport0";
       interrupt-parent = <&gic>;
       interrupts = <GIC_SPI 117 IRQ_TYPE_LEVEL_HIGH>,
                    <GIC_SPI 118 IRQ_TYPE_LEVEL_HIGH>;
       clock-div = <1>;
       status = "disabled";
   };

Board-specific device tree (``sc594-som-ezkit.dts``) enables the peripheral:

.. code-block:: text

.. code-block:: dts

   &lp0 {
       pinctrl-names = "default";
       pinctrl-0 = <&lp0_default>;
       status = "okay";
   };

Allocating a peripheral to SHARC
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To allocate a peripheral to SHARC cores, modify the board-specific device tree. The ARM core still configures the pinmux but doesn't interact with the peripheral otherwise.

**Example: Allocating Linkport0 to SHARC**

**Step 1:** Disable the peripheral in Linux by setting ``status = "disabled"`` in the board device tree:

.. code-block:: text

.. code-block:: dts

   &lp0 {
       pinctrl-names = "default";
       pinctrl-0 = <&lp0_default>;
       status = "disabled";
   };

**Step 2:** Configure the pinmux for the peripheral using the ``icc`` (inter-core communication) driver:

.. code-block:: text

.. code-block:: dts

   &pinctrl0 {
       icc {
           icc_default: icc0@0 {
               adi,group = "lp0grp";
               adi,function = "lp0";
           };
       };
   };

**Step 3:** Apply the pinmux configuration through the ``icc`` driver:

.. code-block:: text

.. code-block:: dts

   &icc0 {
       pinctrl-names = "default";
       pinctrl-0 = <&icc_default>;
       status = "okay";
   };

**Result:** Linux configures the pinmux at boot but doesn't access the peripheral. The SHARC core can use Linkport0 exclusively without interference.

.. note::
   After modifying device trees, rebuild the kernel and deploy the updated device tree blob (DTB) to your target. The changes take effect on the next boot.

**See also:** :doc:`Configuring System Memory <../development/Configuring-System-Memory-When-Using-Linux-and-SHARC-Applications>` for memory partitioning between cores.

----

Additional Resources
====================

* **Documentation**: :doc:`Development Guide <../development/Development>` | :doc:`Examples <../examples/Examples>`
* **Support**: `EngineerZone Forum <https://ez.analog.com/linux-software-drivers>`_ | `GitHub Issues <https://github.com/analogdevicesinc/lnxdsp-adi-meta/issues>`_
* **Release Information**: :doc:`5.0.1 Landing Page <../Linux-for-ADSP‐SC5xx-5.0.1-Landing-Page>` | :doc:`Older Releases <../Older-Releases>`