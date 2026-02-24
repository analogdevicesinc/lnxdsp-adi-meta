================
The Kitchen Sink
================

This image is fully loaded with all the features offered with the full image with the addition of a few notable packages.
This example is intended to exhibit the versatility of an open source build and operating system, coupled with our
ARM-SHARC SoC can allow the user to leverage some extremely complex readily available software.

Building the image
------------------

Go to ``sources/meta/meta-adi/tools`` sources and run

.. code-block:: shell

   machine :: sources/meta-adi/tools » git checkout kitchen_sink
   machine :: sources/meta-adi/tools » chmod +x prepare-kitchen-sink.sh
   machine :: sources/meta-adi/tools » ./prepare-kitchen-sink.sh
   machine kitchen sink sources...
   Cloning Xilinx Jupyter meta layer...
   Cloning into 'meta-xilinx'...
   remote: Enumerating objects: 1687, done.
   remote: Counting objects: 100% (473/473), done.
   remote: Compressing objects: 100% (208/208), done.
   remote: Total 1687 (delta 266), reused 420 (delta 258), pack-reused 1214 (from 1)
   Receiving objects: 100% (1687/1687), 265.26 KiB | 3.12 MiB/s, done.
   Resolving deltas: 100% (1036/1036), done.
   Patching Xilinx meta layer...
   Patching OE meta layer...
   Done
   Please proceed to setup environment and build the image.

This will clone and patch the relevant repositories required for compiling the kitchen-sink image.

Go back to the yocto directory and create the build directory as usual with setup-environment, then build ``sc5xx-kitchen-sink``

.. code-block:: shell

   machine :: yocto » source setup-environment -m adsp-sc598-som-ezkit
   Your build environment has been configured with:

           MACHINE=adsp-sc598-som-ezkit

   You can now run 'bitbake <target>'
   Some of common targets are:
           u-boot-adi
           linux-adi
           adsp-sc5xx-ramdisk-emmc-tools
           adsp-sc5xx-kitchen-sink
           adsp-sc5xx-minimal
           adsp-sc5xx-minimal-mmc
           adsp-sc5xx-full
           adsp-sc5xx-ramdisk
           adsp-sc5xx-tiny

This can now either be booted via NFS/eMMC/SD card boot (spiboot is not available due to image size).

Neofetch
--------

This is a commonly found package within package managers which showcases some details about the current Linux Distribution and hardware
specifications of the machine. The support is however not maintained for Yocto. Since we are working in an open source environment however, we
can add our `own instructions for Yocto <https://github.com/analogdevicesinc/lnxdsp-adi-meta/blob/glencos%2Bjupyter/meta-adi-adsp-sc5xx/recipes-adi/neofetch/neofetch.bb>`_ to include it.

.. code-block:: shell

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

   adsp-sc598-som-ezkit login: root
   Password:

        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@        -------------------------
        @@@@@@@@  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@        OS: Analog Devices Inc Reference Distro (glibc) 5.0.0 (scarthgap) aarch64
        @@@@@@@@     @@@@@@@@@@@@@@@@@@@@@@@@@@        Host: ADI 64-bit SC598 SOM EZ Kit
        @@@@@@@@        @@@@@@@@@@@@@@@@@@@@@@@        Kernel: 6.12.0-yocto-standard-00061-g6d25618933d1
        @@@@@@@@            @@@@@@@@@@@@@@@@@@@        Uptime: 1 min
        @@@@@@@@               @@@@@@@@@@@@@@@@        Shell: sh
        @@@@@@@@                   @@@@@@@@@@@@        CPU: (1)
        @@@@@@@@                     @@@@@@@@@@        Memory: 54MiB / 202MiB
        @@@@@@@@                        @@@@@@@
        @@@@@@@@                     @@@@@@@@@@
        @@@@@@@@                   @@@@@@@@@@@@
        @@@@@@@@               @@@@@@@@@@@@@@@@
        @@@@@@@@            @@@@@@@@@@@@@@@@@@@
        @@@@@@@@        @@@@@@@@@@@@@@@@@@@@@@@
        @@@@@@@@     @@@@@@@@@@@@@@@@@@@@@@@@@@
        @@@@@@@@  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

   root@adsp-sc598-som-ezkit:~#

PyRPMsg
-------

This is a simple python library written from scratch that exhibits the Linux RPMsg interface as an interact-able shell, allowing fast prototyping/testing for SHARC firmware. Similar to Neofetch, there is no support for this package since it is something completely custom and as a result, we have added `support for it <https://github.com/analogdevicesinc/lnxdsp-adi-meta/tree/glencos%2Bjupyter/meta-adi-adsp-sc5xx/recipes-adi/python3-pyrpmsg>`_.

Usage instructions can be found `here <https://github.com/analogdevicesinc/lnxdsp-adi-meta/tree/glencos%2Bjupyter/meta-adi-adsp-sc5xx/recipes-adi/python3-pyrpmsg/python3-pyrpmsg/source>`_.

.. warning::
   This is NOT maintained and should ONLY be used for evaluation purposes. It is meant to be an example for including custom userspace programs in the meta layer

JupyterLab
----------

This is the de facto interactive python notebook and is a complex piece of software. Getting such functionality enabled, out of the box, with custom bare metal software would incur significant costs.

Since this package is available for Linux (like several other larger projects) and support is maintained for Yocto by `Xilinx <https://github.com/Xilinx/meta-jupyter>`_, we have the option to supplement our build with it via `a few changes <https://github.com/analogdevicesinc/lnxdsp-adi-meta/blob/glencos%2Bjupyter/meta-adi-adsp-sc5xx/recipes-adi/images/adsp-sc5xx-kitchen-sink.bb>`_.

You can connect to the jupyter-lab using port forwarding. On your local machine, run the following to connect to the board via SSH:

.. code-block:: shell

   ssh -L 9091:localhost:9091 root@10.42.0.109

Once connected, this should now be forwarding the port 9091 from the board to your local machine.

On the board, start jupyter lab:

.. code-block:: shell

   root@adsp-sc598-som-ezkit:~# jupyter-lab --allow-root --no-browser --port=9091
   [I 2024-02-27 17:32:45.013 ServerApp] jupyterlab | extension was successfully linked.
   [I 2024-02-27 17:32:45.057 ServerApp] nbclassic | extension was successfully linked.
   [I 2024-02-27 17:32:49.281 ServerApp] notebook_shim | extension was successfully linked.
   [I 2024-02-27 17:32:49.568 ServerApp] notebook_shim | extension was successfully loaded.
   [I 2024-02-27 17:32:49.574 LabApp] JupyterLab extension loaded from /usr/lib/python3.12/site-packages/jupyterlab
   [I 2024-02-27 17:32:49.574 LabApp] JupyterLab application directory is /usr/share/jupyter/lab
   [I 2024-02-27 17:32:49.602 ServerApp] jupyterlab | extension was successfully loaded.
   [I 2024-02-27 17:32:49.666 ServerApp] nbclassic | extension was successfully loaded.
   [I 2024-02-27 17:32:49.734 ServerApp] Serving notebooks from local directory: /root
   [I 2024-02-27 17:32:49.734 ServerApp] Jupyter Server 1.18.1 is running at:
   [I 2024-02-27 17:32:49.735 ServerApp] http://localhost:8080/lab?token=3943b8d4efaf585f2d06e30dae719eece5102ca5878d0493
   [I 2024-02-27 17:32:49.735 ServerApp]  or http://127.0.0.1:9091/lab?token=3943b8d4efaf585f2d06e30dae719eece5102ca5878d0493
   [I 2024-02-27 17:32:49.736 ServerApp] Use Control-C to stop this server and shut down all kernels (twice to skip confirmation).
   [C 2024-02-27 17:32:49.780 ServerApp]

       To access the server, open this file in a browser:
           file:///root/.local/share/jupyter/runtime/jpserver-373-open.html
       Or copy and paste one of these URLs:
           http://localhost:9091/lab?token=3943b8d4efaf585f2d06e30dae719eece5102ca5878d0493
        or http://127.0.0.1:9091/lab?token=3943b8d4efaf585f2d06e30dae719eece5102ca5878d0493

You should now be able to access the instance from your local machine from ``http://localhost:9091``

NOTE: If you cannot access a browser on your build machine, you can daisy chain the port forwarding via multiple SSH sessions.

You should be able to see the following page:

.. image:: https://github.com/user-attachments/assets/7468f9e3-5848-49b5-9027-24a856872eaa
