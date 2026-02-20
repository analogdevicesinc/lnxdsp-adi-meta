Linux for ADSP-SC5xx Processors 5.0.0
======================================

Welcome to version 5.0.0 of Linux for ADSP-SC5xx.
This collection of pages contains all you need to know in order to download, build, develop and install Linux, running on the ARM core of the ADSP-SC5xx family of processors.

Release Status
--------------

The following boards are supported on this release.

.. list-table::
   :header-rows: 1
   :widths: 40 20 40

   * - Development Board
     - Board Revision
     - Getting Started Guide
   * - `ADSP-SC598 SOM <https://www.analog.com/en/design-center/evaluation-hardware-and-software/evaluation-boards-kits/EV-SC598-SOM.html>`_ and `EV-SOMCRR-EZKIT <https://www.analog.com/en/design-center/evaluation-hardware-and-software/evaluation-boards-kits/ev-somcrr-ezkit.html>`_ carrier board
     - Rev A/B/C/D/E (DSP-SC598 SOM), Rev A/D (REV-SOMCRR-EZKIT)
     - `Getting Started with ADSP-SC598 <getting-started/Getting-Started-with-ADSP‐SC598-(Linux-for-ADSP‐SC5xx-Processors-5.0.0)>`
   * - `ADSP-SC594 SOM <https://www.analog.com/en/design-center/evaluation-hardware-and-software/evaluation-boards-kits/EV-SC594-SOM.html>`_ and `EV-SOMCRR-EZKIT <https://www.analog.com/en/design-center/evaluation-hardware-and-software/evaluation-boards-kits/ev-somcrr-ezkit.html>`_ carrier board
     - 1.0 Rev B or later
     - `Getting Started with ADSP-SC594 <getting-started/Getting-Started-with-ADSP‐SC594-(Linux-for-ADSP‐SC5xx-Processors-5.0.0)>`
   * - `ADSP-SC589-MINI <https://www.analog.com/en/design-center/evaluation-hardware-and-software/evaluation-boards-kits/sharc-audio-module.html>`_
     - 1.5 or later
     - :doc:`Getting Started with ADSP-SC589 MINI <getting-started/Getting-Started-with-ADSP‐SC589‐MINI-(Linux-for-ADSP‐SC5xx-Processors-5.0.0)>`
   * - `ADSP-SC573 EZ-KIT <https://www.analog.com/en/resources/evaluation-hardware-and-software/evaluation-boards-kits/sc573ezkit.html>`_
     - 1.5 or later
     - `Getting Started with ADSP-SC573 EZ-KIT <getting-started/Getting-Started-with-ADSP‐SC573-(Linux-for-ADSP‐SC5xx-Processors-5.0.0)>`

If you do not have an ICE-1/2000 debugger, you can use :doc:`these <boot/Loading-u‐boot-without-an-ICE-debugger-(USB-Debug-Agent)>` steps with the quickstart guide for boards with a usb debug agent (USB DA) port.

Building of the Linux software requires a host PC running a recent flavour Linux.
Analog Devices uses `64-bit Ubuntu 22.04 LTS <https://discourse.ubuntu.com/t/jammy-jellyfish-release-notes/24668>`_ for its building and testing of Linux for ADSP-SC5xx.
If you are new to Linux development we strongly recommend using a host PC with this installation of Linux.
If you are confident in resolving variant specific problems feel free to use your `favourite distro <https://www.techradar.com/uk/best/best-linux-distros>`_.

Release Notes
-------------

Release Notes for the 5.0.0 release can be found `here <https://github.com/analogdevicesinc/lnxdsp-adi-meta/releases/tag/5.0.0-rel>`_.
The test report for this page can be found in `here <test-reports/Test-Report-(5.0.0)>`.