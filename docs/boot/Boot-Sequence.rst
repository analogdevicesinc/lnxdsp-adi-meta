=============
Boot Sequence
=============

Introduction
------------

This page aims to outline the boot sequence of ADSP-SC5xx platforms.

Overview
--------

The Boot ROM is responsible for initial start up - when the board powers on, the cores are brought online, and the Arm core will begin executing from the Boot ROM.

The Boot ROM will then boot the system according to the Boot Mode - set by a rotary switch on the carrier boards, or 3 pins on the SoC more generally. Boot modes 0 through 6 are available, with 7 reserved. They are defined in the HRM of the relevant SoC (Table 47-15 for the SC598, for example) as follows:

.. list-table:: Boot Modes
   :header-rows: 1
   :widths: 10 10 10 10 15 45

   * - Number (SC57x) `(HRM) <https://www.analog.com/media/en/dsp-documentation/processor-manuals/adsp-sc57x-2157x_hwr.pdf#page=2696>`_
     - Number (SC58x) `(HRM) <https://www.analog.com/media/en/dsp-documentation/processor-manuals/SC58x-2158x-hrm.pdf#page=3440>`_
     - Number (SC591/2/4) `(HRM) <https://www.analog.com/media/en/dsp-documentation/processor-manuals/adsp-2159x-sc591-592-594-hrm.pdf#page=2951>`_
     - Number (SC596/8) `(HRM) <https://www.analog.com/media/en/dsp-documentation/processor-manuals/adsp-sc595-sc596-sc598-hrm.pdf#page=3501>`_
     - Boot Source
     - Description
   * - 0
     - 0
     - 0
     - 0
     - No Boot
     - The processor does not boot. Rather the boot kernel executes some of the preboot operations then enters an endless WFI/IDLE state. Used generally when there is no other boot source available, e.g. during first setup or if the usual boot source is corrupted, or in debugging. JTAG or another debugging connection can then be used to boot the system.
   * - 1
     - 1
     - 1
     - 1
     - SPI Controller Boot
     - Boot from the onboard SPI Flash memory. The most commonly used mode to boot U-Boot and Linux.
   * - 2
     - 2
     - 2
     - 2
     - SPI Target Boot
     - Boot from the SPI interface, configured as a target.
   * - 3
     - 7
     - 3
     - 3
     - UART Boot
     - Boot through the UART interface, configured as a target.
   * - 4
     - 6
     - 4
     - 4
     - LINKPORT Boot
     - Boot through the LINKPORT peripheral, configured as a target. Only available on some systems.
   * - \-
     - \-
     - 5
     - 5
     - OSPI Controller Boot
     - Boot from the onboard Octal SPI Flash memory. Only available on systems with OSPI.
   * - \-
     - \-
     - \-
     - 6
     - eMMC Controller Boot
     - Boot from an eMMC chip or card.
   * - 5,6,7
     - 3,4,5
     - 6,7
     - 7
     - Reserved
     - \-

Note, there are *Controller* and *Target* boot modes. In *Target* boot modes, the processor functions as a target to a host device. In these modes, the host device usually applies the reset sequence and waits until the processor is ready to boot, depending on the peripheral in use, and transmits the boot stream data to the processor. In *Controller* boot modes, the processor controls the peripheral and requests data via the peripheral as and when required.

Below is a flowchart that gives an overview of the U-Boot/Linux boot process in some of these modes.

.. figure:: https://github.com/user-attachments/assets/e16d6c26-9f5b-429f-9f43-d78f8f6edb3e
   :alt: Boot Order Overview Flowchart

   Boot Order Overview Flowchart

SHARC Core initialisation
-------------------------

There are two main ways the SHARC cores can be brought online, both of which use the `remoteproc framework <https://docs.kernel.org/staging/remoteproc.html>`_:

- In U-Boot, using the ``rproc`` command, leaving communication with the Linux Kernel to the rpmsg framework.
- Within Linux, using the Linux remoteproc driver.

U-Boot
~~~~~~

U-Boot does not perform any intialisation during boot - instead, the ``rproc`` command is used after booting into the U-Boot shell. When the ``rproc init`` command is run, U-Boot will probe any available SHARC cores and initialise them. They can then be brought online by loading firmware with ``rproc load ...`` and ``rproc start <n>``.

Currently, only ``init``, ``list``, ``start`` and ``load`` are supported by U-Boot for ADSP-SC5xx. No documentation for these commands exists online, but the following is the help information from the U-Boot shell for these commands:

.. code-block:: none

   rproc - Control operation of remote processors in an SoC

   Usage:
   rproc  [init|list|load|start|stop|reset|is_running|ping]
                    Where:
                   [addr] is a memory address
                   <id> is a numerical identifier for the remote processor
                        provided by 'list' command.
                   Note: Remote processors must be initalized prior to usage
                   Note: Services are dependent on the driver capability
                         'list' command shows the capability of each device

           Subcommands:
           init <id> - Enumerate and initalize the remote processor.
                             if id is not passed, initialize all the remote prcessors
           list   - list available remote processors
           load <id> [addr] [size]- Load the remote processor with binary
                             image stored at address [addr] in memory
           start <id>      - Start the remote processor(must be loaded)

`Technical information on U-Boot's remoteproc support <https://docs.u-boot.org/en/latest/develop/driver-model/remoteproc-framework.html>`_ is available in the U-Boot developer documentation.

Linux
~~~~~

Overview
^^^^^^^^

After the system has booted the kernel and mounted the initramfs, the remoteproc driver is initialised. It first probes the cores, before loading default firmware (located in ``/lib/firmware``, both in the initramfs and in the normal root filesystem), normally ``adi_adsp_core<n>_fw.ldr``.

The rpmsg interface is then brought online to enable communication with the cores, creating a communication channel for each core.

The firmware of the cores can be controlled from within Linux using the remoteproc interface, under ``/sys/class/remoteproc/remoteproc<core n>/:doc:``. For example usage, see `"Using Linux (remoteproc)" <../examples/SHARC‐ALSA-Example>` in the SHARC-ALSA example.

In More Detail
^^^^^^^^^^^^^^

*Available* :doc:`here <core1bootlog>` *is an excerpt of Linux's output at boot, showing the startup of SHARC Core1*

- Initial bring-up occurs within the probe function of the ADI remoteproc driver - `adi_remoteproc_probe <https://github.com/analogdevicesinc/lnxdsp-linux/blob/c4403f406eff867723e10acf414afdfe8132102f/drivers/remoteproc/adi_remoteproc.c#L809>`_. This makes a number of calls to the core Linux remoteproc driver:

  - `rproc_alloc <https://github.com/analogdevicesinc/lnxdsp-linux/blob/c4403f406eff867723e10acf414afdfe8132102f/drivers/remoteproc/remoteproc_core.c#L2515>`_ to allocate a remoteproc handle
  - `rproc_add <https://github.com/analogdevicesinc/lnxdsp-linux/blob/c4403f406eff867723e10acf414afdfe8132102f/drivers/remoteproc/remoteproc_core.c#L2341>`_ to register the device, which also calls `rproc_validate <https://github.com/analogdevicesinc/lnxdsp-linux/blob/c4403f406eff867723e10acf414afdfe8132102f/drivers/remoteproc/remoteproc_core.c#L2284>`_ to ensure the device can be started by the driver.

    - e.g. if the device is offline and has no start function, ``rproc_validate`` will fail.
    - If this succeeds, ``<device name> is available`` is printed.
    - Finally, `rproc_boot <https://github.com/analogdevicesinc/lnxdsp-linux/blob/c4403f406eff867723e10acf414afdfe8132102f/drivers/remoteproc/remoteproc_core.c#L1994>`_ is triggered, and the core is powered up.

After ``rproc_boot``, firmware loading begins. ``adi_adsp_core<core n>_fw.ldr`` is loaded from the initramfs by `request_firmware <https://github.com/analogdevicesinc/lnxdsp-linux/blob/c4403f406eff867723e10acf414afdfe8132102f/drivers/base/firmware_loader/main.c#L888>`_ (from Linux's generic firmware loader driver), then `rproc_start <https://github.com/analogdevicesinc/lnxdsp-linux/blob/c4403f406eff867723e10acf414afdfe8132102f/drivers/remoteproc/remoteproc_core.c#L1376>`_ is called, passing the loaded firmware. The firmware is then loaded onto the core over DMA, using `ldr_load <https://github.com/analogdevicesinc/lnxdsp-linux/blob/c4403f406eff867723e10acf414afdfe8132102f/drivers/remoteproc/adi_remoteproc.c#L242>`_, as well as the resource table (which was loaded during probing). Finally, the core is reset and started, and begins running the default firmware.
