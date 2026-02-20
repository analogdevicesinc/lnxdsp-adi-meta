================
What is RPMsg?
================

RPMsg is a communication protocol for heterogeneous inter-processor communication. These processors are often on the same SoC but isolated from each other via software (for instance, a board may boot up Linux with an Arm processor while additional microcontrollers (remote processors) run an RTOS or some bare-metal application).

This page aims to establish an introduction to the inner workings of RPMsg and how to execute a basic test for it. Please note that this is by no means a definitive guide on any RPMsg implementation (eg Linux, OpenAMP, RPMsgLite, etc) and for a deeper understanding, it is encouraged to go through the respective project's dedicated documentation.

RPMsg Working
=============

Considering one of ADI's DSP offerings as an example: the `ADSP-SC598 <https://www.analog.com/en/products/adsp-sc598.html>`_. This board contains 2 SHARC+ processing cores and 1 Arm Cortex A55, where the Arm processor is used to run Linux and is isolated, via software, from both the SHARC cores. All processors however share the memory provided to the system (L2 and L3).

In this scenario, we will aim to establish communication between a SHARC core and the cortex-A55. Both sides will require clients to handle RPMsg related activity. For the Arm processor, since it is running Linux, the kernel includes support for RPMsg via a `dedicated driver <https://docs.kernel.org/staging/rpmsg.html>`_.

The latter, however, is currently assumed to be running bare-metal applications/RTOS only. Due to this, a lightweight, bare-metal library called `rpmsg-lite <https://github.com/analogdevicesinc/rpmsg-lite.git>`_ is utilized. It implements a subset of features from Linux's RPMsg implementation to allow devices such as small MCUs to utilize this framework.

Although RPMsg-based communication is possible between any combination of processors (Arm-SHARC/SHARC-SHARC), the following is discussed with respect to Arm-SHARC interactions.

Fundamental workings
--------------------

The communication takes place via shared memory, and hence, these processors share and know certain regions in the common memory space to search for the relevant data structures. Depending on which processor gets access to the memory first, it will assume the role of the "main" processor, while the other assumes the role of the "sub" processor. The main processor populates the MCAPI region with relevant data structures (such as the resource table) indicating its presence as well as determines the memory regions to be used for the inter-processor communication. Once the "sub" boots up, it discovers the MCAPI region has already been populated, parses the data structures and sets a flag in the region to indicate that it is ready to establish communication.

It is now assumed that the role of the main processor was assumed by the Arm chip. Since the Arm processor needs to communicate to the SHARC core, it will have to go through the following steps and establish a channel:

* Establish an RPMsg endpoint
* Announce its presence to the remote processor (sends the source address and endpoint name)
* Remote processor then establishes its own endpoint and a channel using the metadata obtained from the announcement

It should be noted that a channel is a software concept and hence, a single device can have multiple channels. Each channel is tied to endpoints, which are then tied to particular function callbacks. Once created, they do not need to be re-established.

.. image:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/141642367/9e08142a-147c-4aed-8c1f-48e48a80a461
   :width: 400

When the Arm processor wants to communicate via the abovementioned channel, it populates the shared memory region associated with the channel and triggers and interrupt on the SHARC core via the TRU, notifying it about the change made. The SHARC core can now update its values and process the new changes accordingly. The inverse is also true when a SHARC core wants to communicate to the Arm processor.

RPMsg and Linux
---------------

Before discussing the RPMsg interaction between a SHARC core running a baremetal application and an Arm core running linux, it is essential that the boot process of the machine in focus is discussed since it also provides a couple of options to the user, allowing changing the "main" and "sub" processors.

Boot process
~~~~~~~~~~~~

When a board is powered up, the following takes place:

* The processors reads the bootrom and runs some pre-requisite tasks before each boot
* The processor then loads the first stage bootloader(SPL) from a fixed memory address
* SPL then passes the control to the second stage bootloader, or the full version of uboot in this case

.. image:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/141642367/5343408e-6fd5-4c8c-b539-116dd58dde1b
   :width: 400

After this point, there are two paths that the user can take:

1. Let linux boot as normal

   During initialization of the remoteproc driver, MCAPI region is populated, and the SHARC processor is initialized, forcing it to take the role of "sub"

2. Parse and Load the firmware for the SHARC core and run it

   This will enable the MCAPI region to be populated by the SHARC core and will allow it to assume the role of "main"

.. image:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/141642367/ddbfcefe-d151-4151-9201-e31f464c488b
   :width: 800

.. note::

   In both cases, the SHARC core is initialized by sending a binary to it and running it.

Once the MCAPI interface has been populated and metadata exchanged, the RPMsg driver initializes similarly to the previous section. For security purposes, Linux creates RPMsg devices for each RPMsg channel. These are dynamically created, following which, the system continues to boot as usual.

It is recommended that the source code for the respective driver is referred to from `ADI's ADSP Linux repository <https://github.com/analogdevicesinc/lnxdsp-linux>`_ for a deeper understanding.

Testing
=======

Testing can be conducted via following methods:

1. Include relevant packages in your Yocto distribution
2. Build from scratch using the RPMsg guides listed in the `rpmsg-utils <https://github.com/analogdevicesinc/rpmsg-utils>`_ and `rpmsg-examples <https://github.com/analogdevicesinc/rpmsg-examples>`_ repositories.

Since this is an introductory guide, it will only document the former approach. For a more in-depth usage, it is encouraged for the user to refer to the abovementioned repositories.

RPMsg with yocto
----------------

1. In your local.conf file, append the following: ``INSTALL_IMAGE:append = " rpmsg-utils "``

.. note::

   Make sure ``DISTRO_FEATURES:append = " linux_only_audio "`` is present in your local.conf

2. Booting into Linux, the following messages should be visible in the kernel boot log:

.. code-block:: console

   [    2.153877] adi_remoteproc 28240000.core1-rproc: Resource table set, enable rpmsg
   [    2.161710] adi_remoteproc 28240000.core1-rproc: Load verification enabled
   [    2.168504] mmc0: SDHCI controller on 310c7000.mmc [310c7000.mmc] using ADMA
   [    2.175711] remoteproc remoteproc0: core1-rproc is available
   [    2.182065] adi_remoteproc 28a40000.core2-rproc: Resource table set, enable rpmsg
   [    2.189794] remoteproc remoteproc0: powering up core1-rproc
   [    2.195336] remoteproc remoteproc0: Booting fw image adi_adsp_core1_fw.ldr, size 32228
   [    2.203461] adi_remoteproc 28a40000.core2-rproc: Load verification enabled
   [    2.212513] remoteproc remoteproc1: core2-rproc is available
   [    2.220244] remoteproc remoteproc1: powering up core2-rproc
   [    2.228828] remoteproc remoteproc1: Booting fw image adi_adsp_core2_fw.ldr, size 3222

.. note::

   As it can be seen, the first boot method (as documented above) is utilized here, where remoteproc is responsible for populating the MCAPI region, load and execute the required firmware into the SHARC cores.

.. code-block:: console

   [    2.246718] virtio_rpmsg_bus virtio0: creating channel sharc-echo addr 0x97
   [    2.253692] virtio_rpmsg_bus virtio0: creating channel sharc-echo-cap addr 0xa1
   [    2.261025] virtio_rpmsg_bus virtio0: rpmsg host is online
   [    2.266446]  remoteproc0#vdev0buffer: registered virtio0 (type 7)
   [    2.272483] virtio_rpmsg_bus virtio1: rpmsg host is online
   [    2.272524] virtio_rpmsg_bus virtio1: creating channel sharc-echo addr 0x98
   [    2.284931] virtio_rpmsg_bus virtio1: creating channel sharc-echo-cap addr 0xa2
   [    2.292355]  remoteproc1#vdev0buffer: registered virtio1 (type 7)
   [    2.298370] remoteproc remoteproc0: remote processor core1-rproc is now up
   [    2.305218] remoteproc remoteproc1: remote processor core2-rproc is now up

.. note::

   These messages indicate that RPMsg has now created channels dynamically with the SHARC cores using the virtio framework (establishing shared memory communication).

3. Run the following commands to bind an RPMsg device to the rpmsg-char driver:

.. code-block:: console

   root@adsp-sc598-som-ezkit:~# rpmsg-bind-chardev -p virtio0.sharc-echo.-1. -n 1 -e 151 -s 100
   root@adsp-sc598-som-ezkit:~# rpmsg-bind-chardev -p virtio0.sharc-echo-cap.-1. -n 1 -e 352 -s 101
   root@adsp-sc598-som-ezkit:~# rpmsg-bind-chardev -p virtio1.sharc-echo.-1. -n 1 -e 320 -s 102
   root@adsp-sc598-som-ezkit:~# rpmsg-bind-chardev -p virtio1.sharc-echo-cap.-1. -n 1 -e 384 -s 103

4. Test the mechanism with an echo example

.. code-block:: shell

   root@adsp-sc598-som-ezkit:~# echo hello | rpmsg-xmit -n 5 /dev/rpmsg0
   hello => echo from Core1
   root@adsp-sc598-som-ezkit:~# echo hello | rpmsg-xmit -n 5 /dev/rpmsg1
   HELLO => capitalized echo from Core1
   root@adsp-sc598-som-ezkit:~# echo hello | rpmsg-xmit -n 5 /dev/rpmsg2
   hello => echo from Core2
   root@adsp-sc598-som-ezkit:~# echo hello | rpmsg-xmit -n 5 /dev/rpmsg3
   HELLO => capitalized echo from Core2