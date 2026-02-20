Linux for ADSP-SC5xx Processors
================================

Welcome to the official Linux documentation for `Analog Devices <https://www.analog.com>`_ ADSP-SC5xx processors.

ADSP-SC5xx System-on-Chips (SoCs) combine ARM Cortex-A55/A5 application processors with SHARC+ DSP cores, delivering a unique heterogeneous computing platform. These processors excel at real-time audio processing, industrial control, and automotive applications by seamlessly blending Linux's rich ecosystem on the ARM core with deterministic, high-performance signal processing on the SHARC+ DSPs. The architecture enables developers to partition their applications optimally - running high-level application code, networking, and user interfaces on Linux while offloading compute-intensive, real-time algorithms to the dedicated DSPs.

**Platform Highlights**

* **Audio Excellence** - SHARC-ALSA framework, USB Audio Gadget, world-class DSP processing capabilities
* **Performance** - Hardware-accelerated operations, optimised drivers, benchmark-proven throughput
* **Security** - Secure boot support, cryptographic acceleration, trusted execution environment
* **Development Tools** - Yocto-based build system, comprehensive examples, extensive debugging support
* **Connectivity** - Ethernet, USB, SPI, I2C, UART, CAN, and more industry-standard interfaces
* **Real-time Processing** - Dedicated SHARC+ DSPs for deterministic, low-latency processing

.. note::
   📌 **Latest Release**: Linux for ADSP-SC5xx **5.0.1** - :doc:`Release Landing Page <Linux-for-ADSP‐SC5xx-5.0.1-Landing-Page>`

Development Boards
------------------

.. list-table::
   :widths: 30 35 35
   :header-rows: 1

   * - Board
     - Processor Features
     - Quick Start Guide
   * - **SC598-SOM**
     - Cortex-A55 + Dual SHARC+ @ 1GHz
     - :doc:`Getting Started → <getting-started/Getting-Started-with-ADSP‐SC598-(Linux-for-ADSP‐SC5xx-Processors-5.0.0)>`
   * - **SC594-SOM**
     - Cortex-A5 + Single SHARC+ @ 1GHz
     - :doc:`Getting Started → <getting-started/Getting-Started-with-ADSP‐SC594-(Linux-for-ADSP‐SC5xx-Processors-5.0.0)>`
   * - **SC589-MINI**
     - Cortex-A5 + Dual SHARC+ @ 500MHz
     - :doc:`Getting Started → <getting-started/Getting-Started-with-ADSP‐SC589‐MINI-(Linux-for-ADSP‐SC5xx-Processors-5.0.0)>`
   * - **SC573-EZ-KIT**
     - Cortex-A5 + Single SHARC+ @ 500MHz
     - :doc:`Getting Started → <getting-started/Getting-Started-with-ADSP‐SC573-(Linux-for-ADSP‐SC5xx-Processors-5.0.0)>`

Quick Start
-----------

**🏃 Getting Started in 4 Steps**

1. :doc:`Setup your host PC <getting-started/Setting-Up-Your-Host-PC>` - Install tools and configure environment
2. **Choose your board** - Select from the processor guides above (SC598, SC594, SC589, or SC573)
3. :doc:`Build Linux image <development/Yocto-Linux-Kernel-development>` - Use Yocto to create your custom image
4. :doc:`Run examples <examples/Examples>` - Try SHARC-ALSA, USB Audio, and more

**🎯 Key Topics**: :doc:`Boot Process <boot/Boot-Sequence>` | :doc:`RPMsg <development/RPMsg>` | :doc:`Memory Configuration <development/Configuring-System-Memory-When-Using-Linux-and-SHARC-Applications>` | :doc:`Secure Boot <secure-boot/Secure-Boot-Support>`

Latest Release
--------------

.. admonition:: Version 5.0.1 - Current Stable
   :class: tip

   **Linux 6.1 LTS** | **Yocto Mickledore** | **Enhanced RPMsg** | **Secure Boot** | **New Audio Examples**

   :doc:`Full Release Notes → <Linux-for-ADSP‐SC5xx-5.0.1-Landing-Page>` | :doc:`Previous Releases <Linux-for-ADSP‐SC5xx-5.0.0-Landing-Page>`

Quick Links
-----------

**Essential Documentation**

* **Setup**: :doc:`Setting Up Your Host PC <getting-started/Setting-Up-Your-Host-PC>` | :doc:`Boot Sequence <boot/Boot-Sequence>`
* **Development**: :doc:`Yocto Build System <development/Yocto-Linux-Kernel-development>` | :doc:`RPMsg Communication <development/RPMsg>`
* **Examples**: :doc:`SHARC-ALSA Audio <examples/SHARC‐ALSA-Example>` | :doc:`USB Audio Gadget <examples/USB-Gadget-Audio-Example>`
* **Support**: :doc:`FAQ <faq/Frequently-Asked-Questions>` | `Forum <https://ez.analog.com/linux-software-drivers>`_ | `Issues <https://github.com/analogdevicesinc/lnxdsp-adi-meta/issues>`_

Resources
---------

**📦 Source Code**: `lnxdsp-adi-meta <https://github.com/analogdevicesinc/lnxdsp-adi-meta>`_ | `linux-adi <https://github.com/analogdevicesinc/linux-adi>`_ | `u-boot-adi <https://github.com/analogdevicesinc/u-boot-adi>`_

**📖 Support**: `Hardware Docs <https://www.analog.com/en/products/adsp-sc598.html>`_ | `EngineerZone <https://ez.analog.com/linux-software-drivers>`_ | `Technical Support <https://www.analog.com/en/support/technical-support.html>`_

Documentation
-------------

.. toctree::
   :maxdepth: 0
   :hidden:
   :titlesonly:

   5.0.1 Landing Page <Linux-for-ADSP‐SC5xx-5.0.1-Landing-Page>
   5.0.0 Landing Page <Linux-for-ADSP‐SC5xx-5.0.0-Landing-Page>
   Examples <examples/Examples>
   Development <development/Yocto-Linux-Kernel-development>
   Boot Sequence <boot/Boot-Sequence>
   Benchmarks <benchmarks/Benchmarking>
   FAQ <faq/Frequently-Asked-Questions>
   Secure Boot <secure-boot/Secure-Boot-Support>
   Test Reports <test-reports/Test-Report-(5.0.1)>

.. tip::
   🔍 **Quick Search**: Press :kbd:`Ctrl+K` to search documentation | For generic Linux kernel info, visit `docs.kernel.org <https://docs.kernel.org/>`_

----

.. centered:: **Copyright © 2025 Analog Devices, Inc. All rights reserved.**