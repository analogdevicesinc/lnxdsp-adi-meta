===========================
Frequently Asked Questions
===========================

How do I add packages to my own Linux filesystem?
==================================================

Let's try, for example, to add the package ``ethtool`` to your own target adsp-custom-ramdisk image.

Find the Yocto Project recipe
------------------------------

Users could add their own packages instead of the ``ethtool``, the first step is to find out the Yocto Project recipe that includes ``ethtool``. The way to find recipes is to go to the `Openembedded Layer Index <https://layers.openembedded.org/layerindex/branch/master/recipes/>:doc:`_ web site.

The below picture demonstrates how to find the package ``gstreamer`` in this website.

.. image:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/7bfec7fc-df8f-4bad-819c-a7484dbb2075
   :width: 600

Add the Package to the filesystem
----------------------------------

**Method 1**: After finding the specific recipe name, users need to add it to the image by adding this line to ``conf/local.conf``, which is highly recommended:

.. code-block:: shell

   IMAGE_INSTALL:append = "ethtool"

**Method 2**: Users can also add the package into their own custom-recipe.bb file directly. For example, applying the below patch would add the ``ethtool`` package into the ``adsp-custom-ramdisk``'s filesystem.

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

Build the Target Images
-----------------------

Run the below command to bitbake the ramdisk filesystem. The package ``ethtool`` will be deployed into the Linux filesystem directly.

.. code-block:: shell

   bitbake adsp-custom-ramdisk

How do I debug a SHARC application whilst running Linux on the ARM core?
=========================================================================

When attempting to debug SHARC applications using CrossCore Embedded Studio, it is important to ensure that the debug session does not interfere with execution of Linux running on the ARM core of the processor.

Since the ARM is the booting core for the SC5xx processors, Linux will be running when you connect to the Cross Core Embedded Studio debugger.

You will need to make several changes when creating a debug session for the SHARC cores to avoid interfering with Linux.

Ensure that the debug session does not load any preloads or applications to the ARM core
-----------------------------------------------------------------------------------------

By default the debug session will attempt to load applications on to the ARM core.

When creating the debug session you will need to remove any preload or initcode binary and ensure that an application is not loaded. This will ensure that the L2 and L3 memory reserved for the ARM core is not changed by the debug session (Assuming that you have either used the default memory configuration or correctly repartitioned the memory between the cores)

Ensure that the debug session does not reset the processor when loading the SHARC cores
----------------------------------------------------------------------------------------

By default the debug session will reset the processor when starting a debug session. Since this cannot be performed on a core by core basis the whole system is reset, wiping the running Linux from memory.

When creating the debug session you will need to uncheck the option to **"reset on reload"** from the debug session settings.

Ensure that semihosting does not interfere with Linux running on the ARM core
------------------------------------------------------------------------------

When creating a debug session for SC5xx processors by default CrossCore Embedded Studio uses the ARM supervisor call (SVC) instruction to trigger a communication to the host PC. Unfortunately this instruction is used by Linux for other purposes. By leaving this feature active the execution of Linux will become considerably slower and may result in crashes.

When creating the debug session you will need to uncheck the **"Use semihosting"** checkbox from the debug session settings.

This will ensure that the emulator does not halt the board for each execution of the SVC instruction.

How do I develop Linux for ADSP-SC5xx with my own repositories?
================================================================

Host PC setup
-------------

Covered in `Setting Up Your Host PC <../getting-started/Setting-Up-Your-Host-PC>`.

Source Code Preparation
-----------------------

Download Source code
~~~~~~~~~~~~~~~~~~~~

.. code-block:: shell

   $ mkdir ~/linux-dsp-own-repos
   $ cd ~/linux-dsp-own-repos
   $ mkdir bin
   $ curl http://commondatastorage.googleapis.com/git-repo-downloads/repo > ./bin/repo
   $ chmod a+x ./bin/repo
   $ ./bin/repo init \
    -u https://github.com/analogdevicesinc/lnxdsp-repo-manifest.git \
    -b release/yocto-3.0.0 \
    -m release-yocto-3.0.0.xml

Change ``lnxdsp-adi-meta`` and ``lnxdsp-scripts`` to point to your own repo
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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
   +  <remote fetch="$YOUR_REPO_PATH" name="dte"/>

      <project remote="yocto" revision="50f33d3bfebcbfb1538d932fb487cfd789872026" name="poky" path="sources/poky"/> <!-- thud revision -->
      <project remote="oe" revision="4cd3a39f22a2712bfa8fc657d09fe2c7765a4005" name="meta-openembedded" path="sources/meta-openembedded"/> <!-- thud revision -->
   -  <project remote="adigithub" revision="release/yocto-3.0.0" name="lnxdsp-adi-meta" path="sources/meta-adi"/>
   -  <project remote="adigithub" revision="release/yocto-3.0.0" name="lnxdsp-scripts" path="sources">
   +  <project remote="dte" revision="release/yocto-3.0.0" name="lnxdsp-adi-meta" path="sources/meta-adi"/>
   +  <project remote="dte" revision="release/yocto-3.0.0" name="lnxdsp-scripts" path="sources">
   	  <linkfile dest="setup-environment" src="setup-environment"/>
      </project>>

and sync the repo:

.. code-block:: shell

   $ ./bin/repo sync

Change u-boot and linux-kernel URI to point to your own repo
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: shell

   $ cd ~/linux-dsp-own-repos/sources/

Apply the below modifications into your own build script, take ``adsp-sc589-ezkit`` as an example, the same patch should be applied into adsp-sc589-mini, adsp-sc584-ezkit, adsp-sc573-ezkit etc.

.. code-block:: diff

   diff --git a/base/adsp-sc589-ezkit/local.conf b/base/adsp-sc589-ezkit/local.conf
   index 260a16b..b3c69f1 100644
   --- a/base/adsp-sc589-ezkit/local.conf
   +++ b/base/adsp-sc589-ezkit/local.conf
   @@ -21,6 +21,11 @@
    # This sets the default machine to be adsp-sc589-ezkit if no other machine is selected:
    MACHINE ?= "adsp-sc589-ezkit"

   +UBOOT_GIT_URI ?= "git://$YOUR_REPO_PATH/u-boot.git"
   +UBOOT_BRANCH ?= "release/yocto-3.0.0"
   +KERNEL_GIT_URI ?= "git://$YOUR_REPO_PATH/lnxdsp-linux.git"
   +KERNEL_BRANCH ?= "release/yocto-3.0.0"
   +
    #
    # Where to place downloads
    #

Then you can start your development based on your own repos:

.. code-block:: shell

   $ cd ~/linux-dsp-own-repos/
   $ source setup-environment -m adsp-sc589-ezkit
   Your build environment has been configured with:

   MACHINE=adsp-sc589-ezkit

   You can now run 'bitbake <target>'
   Some of common targets are:
   u-boot-adi
   linux-adi
   adsp-sc5xx-full
   adsp-sc5xx-minimal

   $ bitbake ...

How do I allocate a peripheral to the SHARC?
=============================================

Default Peripheral allocation between SHARCs and ARM
-----------------------------------------------------

By default, all peripherals are allocated to the ARM. In order to access a peripheral it is necessary for the **pinmux** for the peripheral to be configured correctly. The pinmux should only be configured by a single core and by default this is handled by the ARM, which is the booting core.
Peripheral allocation is controlled by the device tree source file. The device tree source files are located in the Linux source repo in the ``/arch/arm/boot/dts`` folder. For the SC594 EZKIT there are two device tree source files, a generic one for the device family named ``sc59x.dtsi`` and a board specific one named ``sc594-som-ezkit.dts``.

For ``Linkport0`` for example there will be an entry in both files. The ``sc594.dtsi`` file contains:

.. code-block:: text

   lp0: linkport@0 {
   	compatible = "linkport0";
   	interrupt-parent = <&gic>;
   	interrupts = <GIC_SPI 117 IRQ_TYPE_LEVEL_HIGH>,
   	             <GIC_SPI 118 IRQ_TYPE_LEVEL_HIGH>;
   	clock-div = <1>;
   	status = "disabled";
   };

The ``sc594-som-ezkit.dts`` contains the following entry which overrides the above status and enables the linkport:

.. code-block:: text

   &lp0 {
   	pinctrl-names = "default";
   	pinctrl-0 = <&lp0_default>;
   	status = "okay";
   };

Allocating a peripheral to SHARC
---------------------------------

Allocating a peripheral to SHARC requires changes to the device tree source file specific to the board. The ARM core is still required to configure the pinmux but should otherwise not interact with the peripheral. For example allocating ``Linkport0`` to the SHARC requires the following changes to ``sc594-som-ezkit.dts`` file. First disable Linkport0 in the device tree:

.. code-block:: text

   &lp0 {
   	pinctrl-names = "default";
   	pinctrl-0 = <&lp0_default>;
   	status = "disabled";
   };

Next it is necessary to specify the required pinmux for ``Linkport0``. For any peripherals not used by Linux this is handled by the ``icc`` driver:

.. code-block:: text

   &pinctrl0 {
   	icc {
   		icc_default: icc0@0 {
   			adi,group = "lp0grp";
   			adi,function = "lp0";
   		};
   	};
   };

Lastly, the pincontrol which was just created needs to be passed into the ``icc`` which will then set up the pinmux for ``Linkport0`` use and ensure the pins are reserved. The driver does not interact with the peripheral itself thereby reserving it for the SHARC:

.. code-block:: text

   &icc0 {
   	pinctrl-names = "default";
   	pinctrl-0 = <&icc_default>;
   	status = "okay";
   };

The pinmux for ``Linkport0`` is then configured at boot by Linux and can be used by a SHARC core without Linux accessing the device or the pins for any other purpose.