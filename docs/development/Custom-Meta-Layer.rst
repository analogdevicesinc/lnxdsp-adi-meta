============================
Creating a Custom Meta Layer
============================

Introduction
------------

Yocto builds are organised into layers: collections of metadata (recipes, configuration files,
and patches) that each address a specific concern. The ADI release ships several layers covering
the BSP, kernel, drivers, and example images. Any customisation that belongs to your product,
whether adding packages, patching upstream recipes, or tweaking configuration, should live in a
separate layer that you own and maintain independently of the ADI sources.

This guide creates a minimal ``meta-custom`` layer, registers it with the build system, and
walks through a simple recipe to confirm everything is working.

Prerequisites
-------------

A working Yocto build environment is assumed. If you don't have one set up yet, follow the
:doc:`Getting Started guide <../getting-started/Getting-Started-with-ADSP‐SC598-(Linux-for-ADSP‐SC5xx-Processors-5.0.1)>`
before continuing.

The examples here use the SC598 SOM EZKit (``adsp-sc598-som-ezkit``). The steps are identical
for any other supported board.

Creating the Layer
------------------

Source the build environment first so the Yocto tools are available:

.. code-block:: shell

   cd ~/adsp-sc598-som-ezkit-yocto-build
   source setup-environment -m adsp-sc598-som-ezkit

Sourcing the script changes the working directory to ``build/``. Create the layer in the
``sources`` directory alongside the other layers:

.. code-block:: shell

   bitbake-layers create-layer ../sources/meta-custom

This generates the basic skeleton:

.. code-block:: text

   sources/meta-custom/
   ├── conf/
   │   └── layer.conf
   ├── COPYING.MIT
   └── README

``layer.conf`` is populated with defaults for ``BBFILE_COLLECTIONS``, ``BBFILES``, and
``LAYERSERIES_COMPAT``. No changes are needed there to get started.

Registering the Layer
---------------------

Add the layer to ``build/conf/bblayers.conf``:

.. code-block:: shell

   bitbake-layers add-layer ../sources/meta-custom

To confirm it was registered:

.. code-block:: shell

   bitbake-layers show-layers

``meta-custom`` should appear in the list alongside the existing layers.

A Simple Recipe
---------------

The example below installs a small shell script onto the target. It covers the essentials of
a recipe: declaring a license, referencing local source files, and installing them into the
correct location on the target filesystem.

Create the recipe directory and a ``files`` subdirectory for local sources:

.. code-block:: shell

   mkdir -p ../sources/meta-custom/recipes-custom/hello-adi/files

Create ``../sources/meta-custom/recipes-custom/hello-adi/files/hello-adi.sh``:

.. code-block:: shell

   #!/bin/sh
   echo "Hello from ADSP-SC598"

Create the recipe file at
``../sources/meta-custom/recipes-custom/hello-adi/hello-adi_1.0.bb``:

.. code-block:: shell

   SUMMARY = "Custom example script"
   LICENSE = "MIT"
   LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

   SRC_URI = "file://hello-adi.sh"

   S = "${WORKDIR}"

   do_install() {
       install -d ${D}${bindir}
       install -m 0755 ${WORKDIR}/hello-adi.sh ${D}${bindir}/hello-adi
   }

Including the Recipe in the Image
----------------------------------

Recipes are not automatically included in an image. The simplest way to add ``hello-adi``
to a build is to append to ``IMAGE_INSTALL`` in ``build/conf/local.conf``:

.. code-block:: shell

   IMAGE_INSTALL:append = " hello-adi"

Then build:

.. code-block:: shell

   bitbake adsp-sc5xx-minimal

Once the board is booted, the script will be at ``/usr/bin/hello-adi``:

.. code-block:: text

   root@adsp-sc598-som-ezkit:~# hello-adi
   Hello from ADSP-SC598

Using bbappend
--------------

A ``bbappend`` file lets you modify an existing recipe without touching it directly, keeping
your changes cleanly separated in ``meta-custom``. The ``%`` wildcard in the filename matches
any version of the recipe.

As a simple example, the board's hostname can be changed by appending to ``base-files``, which
is the recipe responsible for ``/etc/hostname``. Create
``../sources/meta-custom/recipes-core/base-files/base-files_%.bbappend``:

.. code-block:: shell

   hostname = "myadspdevboard"

After rebuilding and booting, the shell prompt will reflect the change:

.. code-block:: text

   root@myadspdevboard:~#

For a more complete image recipe example, see ``adsp-custom-ramdisk`` in the
:doc:`FAQ <../faq/Frequently-Asked-Questions>`.
