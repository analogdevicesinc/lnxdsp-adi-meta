============
Test Reports
============

Introduction
============

This section provides detailed test reports for each release of Linux for ADSP-SC5xx processors. These reports document the verification and validation testing performed on various hardware platforms and software components, helping you understand what features have been tested and their status across different board configurations.

Each test report includes comprehensive results for key subsystems including audio drivers, inter-processor communication (RPMsg), peripherals, networking, and more. The reports indicate which features are verified, known limitations, and board-specific considerations.

**Why test reports matter:**

* Verify that critical features work on your specific hardware configuration
* Understand known issues and workarounds before deployment
* Plan your testing strategy based on verified functionality
* Track regression fixes and improvements across releases
* Make informed decisions about board selection for your project

.. note::
   Test reports are cumulative for minor releases (e.g., 5.0.x). Regressions specific to patch releases are explicitly noted within each report.

Available Test Reports
======================

Current Release
---------------

**Version 5.0.1**

Latest test report covering SC598-SOM, SC594-SOM, SC589-MINI, and SC573-EZKIT platforms.

:doc:`5.0.1 Test Report → <Test-Report-(5.0.1)>`

Previous Releases
-----------------

.. list-table::
   :header-rows: 1
   :widths: 20 60 20

   * - Version
     - Release Date
     - Test Report
   * - **5.0.0**
     - 2024
     - :doc:`5.0.0 Test Report <Test-Report-(5.0.0)>`
   * - **3.1.0**
     - 2023
     - :doc:`3.1.0 Test Report <Test-Report-(3.1.0)>`

Understanding Test Results
===========================

Test reports use the following symbols to indicate component status:

* **✅** - Verified working on this platform
* **➖** - Not applicable or not tested on this platform
* **N/A** - Hardware-specific feature not available on this board
* **Known Issue** - Feature has known limitations (see notes in report)

Components Tested
-----------------

Each test report typically covers:

**Audio Subsystem**
  * ADAU1962 audio codec drivers
  * USB Audio Gadget functionality
  * SHARC-ALSA framework (SC598 only)
  * Audio playback and recording paths

**Inter-Processor Communication**
  * remoteproc framework for SHARC+ core management
  * virtio RPMsg for ARM-SHARC communication
  * Message passing and shared memory

**Core Functionality**
  * Common Clock Framework
  * Crypto Framework and hardware acceleration
  * Pinctrl and GPIO subsystems
  * Reset controllers

**Storage and Memory**
  * eMMC/SD card interfaces
  * QSPI/SPI flash support
  * DMA engines

**Networking**
  * Ethernet drivers
  * Network stack validation
  * Performance benchmarks

**Boot and Security**
  * U-Boot bootloader functionality
  * Secure boot (where applicable)
  * Boot mode switching

Using Test Reports
==================

**Before deployment:**

1. Check the test report for your target hardware platform
2. Verify that your required features are marked as tested
3. Review any known issues or limitations
4. Plan workarounds for untested or N/A features

**During development:**

* Use test reports to validate your custom builds
* Reference testing methodology for your own validation
* Report discrepancies or regressions to the development team

**For issue reporting:**

When reporting issues, include:

* Your release version and target board
* Reference to the relevant test report
* Whether the issue reproduces on a known-good configuration

Additional Resources
====================

* **Release Notes**: `GitHub Releases <https://github.com/analogdevicesinc/lnxdsp-adi-meta/releases>`_
* **Issue Tracking**: `GitHub Issues <https://github.com/analogdevicesinc/lnxdsp-adi-meta/issues>`_
* **Support Forum**: `EngineerZone <https://ez.analog.com/linux-software-drivers>`_

.. toctree::
   :maxdepth: 1
   :hidden:

   Test-Report-(5.0.1)
   Test-Report-(5.0.0)
   Test-Report-(3.1.0)
