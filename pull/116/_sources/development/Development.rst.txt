===========
Development
===========

This section provides guides and resources for developing applications and customizing Linux for ADSP-SC5xx processors.

* :doc:`Boot Sequence <Boot-Sequence>`: Understand the boot sequence of ADSP-SC5xx platforms, including boot ROM, boot modes, and SHARC core initialization
* :doc:`U-Boot Environment <U‐Boot-Environment>`: U-Boot environment variables, configuration, and setting boot methods
* :doc:`Debugging without ICE Hardware <Debugging-without-ICE-Hardware>`: Load U-Boot using the USB Debug Agent port when an ICE debugger is unavailable
* :doc:`Linux Kernel Development <Linux-Kernel-Development>`: Compile, modify, and rebuild the Linux kernel for ADSP-SC5xx platforms
* :doc:`Compiling and Running a Rust Kernel <Compiling-and-running-a-Rust-kernel>`: Run the Linux kernel with Rust support on ADSP-SC598 boards (requires Yocto 5.0+)
* :doc:`RPMsg <RPMsg>`: RPMsg protocol for heterogeneous inter-processor communication between ARM and SHARC+ cores
* :doc:`RPMsg-Lite <RPMsg‐Lite>`: RPMsg-Lite for bare metal applications on ARM and SHARC+ cores
* :doc:`Configuring System Memory <Configuring-System-Memory-When-Using-Linux-and-SHARC-Applications>`: Configure and optimize system memory allocation between Linux (ARM) and SHARC+ applications

.. toctree::
   :maxdepth: 0
   :hidden:
   :titlesonly:

   ../getting-started/Setting-Up-Your-Host-PC
   Debugging-without-ICE-Hardware
   Boot-Sequence
   U‐Boot-Environment
   Linux-Kernel-Development
   Compiling-and-running-a-Rust-kernel
   RPMsg
   RPMsg‐Lite
   Configuring-System-Memory-When-Using-Linux-and-SHARC-Applications
