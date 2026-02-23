===========
Development
===========

This section provides guides and resources for developing applications and customizing Linux for ADSP-SC5xx processors. Whether you're building kernel modules, communicating between ARM and SHARC+ cores, or optimizing system memory, you'll find the information you need here.

Kernel Development
------------------

**Linux Kernel Customization**

Learn how to compile, modify, and rebuild the Linux kernel for ADSP-SC5xx platforms.

:doc:`Yocto Linux Kernel Development → <Yocto-Linux-Kernel-development>`

**Rust Kernel Support**

Explore compiling and running the Linux kernel with Rust support on ADSP-SC598 boards (requires Yocto 5.0+).

:doc:`Compiling and Running a Rust Kernel → <Compiling-and-running-a-Rust-kernel>`

Inter-Processor Communication
------------------------------

**RPMsg Protocol**

Understand RPMsg, the communication protocol for heterogeneous inter-processor communication between ARM and SHARC+ cores.

:doc:`What is RPMsg? → <RPMsg>`

**RPMsg-Lite for Bare Metal**

Learn how to use RPMsg-Lite for bare metal applications on ARM and SHARC+ cores.

:doc:`RPMsg-Lite → <RPMsg‐Lite>`

System Configuration
--------------------

**Memory Management**

Configure and optimize system memory allocation between Linux (ARM) and SHARC+ applications.

:doc:`Configuring System Memory → <Configuring-System-Memory-When-Using-Linux-and-SHARC-Applications>`

Additional Resources
--------------------

* **Source Code**: `lnxdsp-adi-meta <https://github.com/analogdevicesinc/lnxdsp-adi-meta>`_ | `linux-adi <https://github.com/analogdevicesinc/linux-adi>`_ | `u-boot-adi <https://github.com/analogdevicesinc/u-boot-adi>`_
* **Examples**: :doc:`Working Examples <../examples/Examples>`
* **Support**: `EngineerZone Forum <https://ez.analog.com/linux-software-drivers>`_ | `GitHub Issues <https://github.com/analogdevicesinc/lnxdsp-adi-meta/issues>`_

.. toctree::
   :maxdepth: 0
   :hidden:
   :titlesonly:

   Yocto-Linux-Kernel-development
   Compiling-and-running-a-Rust-kernel
   RPMsg
   RPMsg‐Lite
   Configuring-System-Memory-When-Using-Linux-and-SHARC-Applications
