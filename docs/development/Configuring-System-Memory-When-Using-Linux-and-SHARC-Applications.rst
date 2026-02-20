================================================================
Configuring System Memory When Using Linux and SHARC Applications
================================================================

Introduction
============

The present document attempts to catalog the different types of memory and the default allocation of it on the ARM & SHARC cores on the the ADSP-SC5xx platforms.

Terminology
-----------

Different types of memory
~~~~~~~~~~~~~~~~~~~~~~~~~~

* **Level 1 (L1)** - Small amount of memory in the core. It's fast, but private to the core
* **Level 2 (L2)** - Larger memory on chip. It's slower than L1 but shared between cores
* **Level 3 (L3)** - External memory chip via controller. Largest but slowest memory, shared between the cores

Ownership
~~~~~~~~~

All memory can be accessed from all cores with exception of the ARM L1 cache. However, each memory space can only be allocated to a single core who becomes the owner of that memory and the project for that core is responsible for populating the memory. If multiple cores are allocated the same memory then corruption may occur.

Memory access properties such as cacheability and write permissions can be configured for each memory space within the **MMU** (ARM) or **cache registers** (SHARC). There is also support to restrict memory access using the **Shared Memory Protection Unit** (**SPU**) in hardware.

LDF Memory Sections
~~~~~~~~~~~~~~~~~~~

The **Linker Description Files** (**LDF**) on SHARC contain 3 sections:

* **Input section** - the section names that come from the source files indicating where the symbol is to be mapped e.g. seg_dmda for DM data
* **Memory sections** - the names of the sections referring to the memory space e.g. ``mem_L2B1P1_bw`` for L2 memory block 1
* **Output section** - a mapping statement in the LDF which maps a series of input sections to a memory section

For all SC5xx parts
~~~~~~~~~~~~~~~~~~~~

* ARM

  * L1 - Used as cache. Cannot be loaded directly for code/data.

* SHARCs

  * Each SHARC core has its own L1
  * This can be configured as a combination of cache, code and data
  * SHARC L1 is typically used for low latency audio algorithms
  * This is what makes SHARC popular

The L2 RAM is memory block that is physically shared by all the cores. This can be used for anything. Out of the box its split 4 ways ARM, SHARC1, SHARC2 and a small section is reserved for inter-core communication (MCAPI or RPMsg). Note that by default Linux does not allocate any memory from the L2 region reserved for it.

Similarly the L3 memory is physically shared by all the cores. It is possible to create a region of shared L3 memory. EZ-KITs vary from model to model. But generally, they have lots. By default, this is split evenly between the cores, no shared memory.

Splitting memory between SHARCs and ARM
========================================

Default Memory allocation between SHARCs and ARM
-------------------------------------------------

The default memory split attempts to split the memory evenly between the available cores, whilst satisfying any restrictions faced by that core. It's likely that your application will have different memory requirements for each core, so you may need to adjust the split manually.

L2 memory also includes:

* a small shared memory at the start of L2 for inter-core communication (ICC)
* an 8K scratch memory at the end of L2 for the boot ROM's working area. This space must be reserved if any boot ROM API's are invoked at run-time, or if the processor is reset. Otherwise, it's possible to use this space for data which isn't initialized at load time (such as a temporary buffer).

The default memory allocation for your device can be seen in ``$Your_CCES_installation_folder/SHARC/ldf/<DEVICE>.ldf`` For ADSP-SC584 (1xARM + 2xSHARC) the description for the L2 split starts at line 420:

.. code-block:: text

   // ----------------------- L2-RAM (2 MBit) -----------------------------------
   // The 256 KB L2 memory has 8 banks partitioned as follows:
   //  bank1  2008_0000  2008_7FFF   4KB uncached - ICC          (mem_L2B1P1_bw)
   //                                4KB uncached - MCAPI ARM    (mem_L2B1P2_bw)
   //                                4KB uncached - MCAPI SHARC1 (mem_L2B1P3_bw)
   //                                4KB uncached - MCAPI SHARC0 (mem_L2B1P4_bw)
   //                               16KB uncached - ARM          (mem_L2B1P5_bw)
   //  bank2  2008_8000  2008_FFFF  32KB cached   - ARM        (mem_L2B2toB4_bw)
   //  bank3  2009_0000  2009_7FFF  32KB cached   - ARM        (mem_L2B2toB4_bw)
   //  bank4  2009_8000  2009_FFFF  32KB cached   - ARM        (mem_L2B2toB4_bw)
   //  bank5  200A_0000  200A_7FFF  32KB cached   - SHARC1       (mem_L2B5B6_bw)
   //  bank6  200A_8000  200A_FFFF  32KB cached   - SHARC1       (mem_L2B5B6_bw)
   //  bank7  200B_0000  200B_7FFF  32KB cached   - SHARC0       (mem_L2B7B8_bw)
   //  bank8  200B_8000  200B_DFFB  24KB cached   - SHARC0       (mem_L2B7B8_bw)
   //         200B_DFFC  200B_FFFF   8KB cached boot code working area

This is immediately followed by the description for L3 as follows:

.. code-block:: text

   // ----------------------- 256MB DMC0(DDR-A) ---------------------------------
   // 256MB DMC0 DDR SDRAM memory is partitioned as follows:
   //   DDR-A part1 : SHARC0 NW code, 3MB
   //   DDR-A part2 : SHARC1 NW code, 3MB
   //   DDR-A part3 : SHARC0 data, 4MB
   //   DDR-A part4 : SHARC0 VISA code, 3MB (reduced for 20000019 workaround)
   //   DDR-A part5 : SHARC1 VISA code, 3MB (reduced for 20000019 workaround)
   //   DDR-A part6 : SHARC0 data, 62MB
   //   DDR-A part7 : SHARC1 data, 66MB
   //   DDR-A part8 : ARM 112MB

.. warning::

   Code execution addresses are restricted for the SHARC cores so the memory allocated cannot be increased or moved.

.. attention::

   The toolchains have no awareness of the mapping used on other cores, so care needs to be taken to ensure that any changes in mapping are reflected across all cores being used. Failure to do so may either result in wasted memory due to gaps or corruption due to overlaps.

Altering L2 allocation on ARM
------------------------------

U-Boot
~~~~~~

All of L2 can be addressed directly from U-Boot and no specific allocation is necessary. To run U-Boot from L2 it is necessary to modify ``CONFIG_SYS_TEXT_BASE`` in ``/include/configs/<BOARD>.h:doc:``. See `Altering the L3 allocation on ARM <Configuring-System-Memory-When-Using-Linux-and-SHARC-Applications>` for further instructions.

Linux
~~~~~

L2 allocation is controlled by the device tree source file. It is set up for each family platform as **sram** sections. The device tree source files are located in the Linux source repo in the ``/arch/arm/boot/dts`` directory. The file will be named as ``<DEVICE_FAMILY>.dtsi`` so for SC584 it is ``sc58x.dtsi``. There are two sections (``sram0`` and ``sram1``)

.. code-block:: text

   sram0: sram-icc@20080000 {
   	compatible = "mmio-sram";
   	#address-cells = <1>;
   	#size-cells = <1>;
   	reg = <0x20080000 0x1000>;
   	ranges = <0 0x20080000 0x1000>;
   };

.. code-block:: text

   sram1: sram-icc@20084000 {
   	compatible = "mmio-sram";
   	#address-cells = <1>;
   	#size-cells = <1>;
   	reg = <0x20084000 0x3B000>;
   	ranges = <0 0x20084000 0x3B000>;
   };

Due to virtual memory mapping in Linux it is necessary to specify which sections the sram memory map driver should utilize.

Altering the L2 allocation on SHARC
------------------------------------

The LDF files for the SHARC cores provide a mapping for each of the split blocks (as described above):

.. code-block:: text

    mem_L2B1P1_bw           { TYPE(BW RAM) START(0x20000000) END(0x20003fff) WIDTH(8) }
    mem_L2B1P2_bw           { TYPE(BW RAM) START(0x20004000) END(0x20007fff) WIDTH(8) }
    mem_L2B1P3_bw           { TYPE(BW RAM) START(0x20008000) END(0x2000bfff) WIDTH(8) }
    mem_L2B1P4_bw           { TYPE(BW RAM) START(0x2000c000) END(0x2000ffff) WIDTH(8) }
    mem_L2B1P5_bw           { TYPE(BW RAM) START(0x20010000) END(0x2001ffff) WIDTH(8) }
    mem_L2B2toB4_bw         { TYPE(BW RAM) START(0x20020000) END(0x2007ffff) WIDTH(8) }
    mem_L2B5B6_bw           { TYPE(BW RAM) START(0x20080000) END(0x200bffff) WIDTH(8) }
    mem_L2B7B8_bw           { TYPE(BW RAM) START(0x200c0000) END(0x200fdffb) WIDTH(8) }
    mem_L2B8BC_bw           { TYPE(BW RAM) START(0x200fdffc) END(0x200fffff) WIDTH(8) }

Followed by 2 macros which are used to determine which blocks are used for cached and non-cached memory in the LDF:

.. code-block:: text

   #define MY_L2_UNCACHED_MEM mem_L2B1P4_bw
   #define MY_L2_CACHED_MEM   mem_L2B7B8_bw

To change the mapping for L2, while maintaining some cached/non-cached areas, just change the start/end addresses of the applicable blocks to match the memory available.

To remove a mapping entirely from L2, the macros should be removed and any output sections referring to the macros should be either be removed or should be updated to refer to another memory section.

Altering the L3 allocation on ARM
----------------------------------

U-Boot
~~~~~~

Part of U-Boot is an init sequence which sets up **DDR** for use. The DDR specific settings for a number of common parts are defined in ``arch/arm/cpu/armv7/<DEVICE_FAMILY>/dmcinit.h``. Should a different DDR device be required the settings for the device can be added to this file. Controlling the DDR3 allocation is done by adjusting ``CONFIG_SYS_SDRAM_BASE``, ``CONFIG_SYS_SDRAM_SIZE`` and ``CONFIG_SYS_TEXT_BASE`` in ``/include/configs/<BOARD>.h``. For the SC584-EZKIT configuration in ``/include/configs/sc584-ezkit.h`` the defaults settings are:

.. code-block:: text

   #define CONFIG_SYS_SDRAM_BASE 89000000
   #define CONFIG_SYS_SDRAM_SIZE 7000000
   #define CONFIG_SYS_TEXT_BASE 89200000

Adjusting the memory allocation of L3 for SC584-EZKIT such that all of it is available for U-Boot running on the ARM core requires ``CONFIG_SYS_SDRAM_BASE`` to be set to ``0x80000000``, ``CONFIG_SYS_SDRAM_SIZE`` to be set to ``0x10000000`` and ``CONFIG_SYS_TEXT_BASE`` to be set to ``0x80200000`` as shown below.

.. code-block:: text

   #define CONFIG_SYS_SDRAM_BASE 80000000
   #define CONFIG_SYS_SDRAM_SIZE 10000000
   #define CONFIG_SYS_TEXT_BASE 80200000

Linux
~~~~~

Adjusting the default L3 allocation to the ARM core running Linux requires a few simple changes to Linux source files as well as the boot args defined in U-Boot. Firstly the device tree source file (.dts) should be adjusted such that the starting address and size match the desired allocation. The device tree source files are located in the Linux source repo in the ``/arch/arm/boot/dts`` directory. The file will be named as ``<BOARD_TYPE>.dts``. Near the top of the file will be a memory section describing the start address and the size available. For SC584-EZKIT for example it will look like:

.. code-block:: text

   memory@89000000 {
   	device_type = "memory";
   	reg = <0x89000000 0x7000000>;
   };

Adjusting the memory allocation of L3 for SC584-EZKIT such that all of it is available for Linux running on the ARM core requires the starting address to be set to ``0x80000000`` and the length to ``0x10000000`` as shown below:

.. code-block:: text

   memory@80000000 {
   	device_type = "memory";
   	reg = <0x80000000 0x10000000>;
   };

The start and end addresses can be found in the ldf file in your CCES installation folder (``SHARC\ldf\ADSP-SC584.ldf``)

When building a compressed kernel it is furthermore necessary to change the address where the kernel is decompressed to. This is configured in ``/arch/arm/mach-<DSP_FAMILY>/Makefile.boot``. Continuing with our SC584-EZKIT example, the file is ``/arch/arm/mach-sc58x/Makefile.boot`` and the relocation addresses are listed for each device type for the SC58X family. The addresses for the ``CONFIG_MACH_SC584_EZKIT`` should be adjusted such that they match the desired start address, but the slight offsets must be retained as shown below.

.. code-block:: text

   zreladdr-y += 0x80008000
   params_phys-y := 0x80000100

See `How the ARM32 Linux kernel decompresses (kernel.org) <https://people.kernel.org/linusw/how-the-arm32-linux-kernel-decompresses>`_ for more information on how kernel decompression takes place on an ARM 32-bit architecture.

Finally, the boot args in U-Boot must be modified such that it passes in the new memory size available to the kernel, which in our example will be:

.. code-block:: text

   mem=256M

Altering the L3 allocation on SHARC
------------------------------------

The LDF for each SHARC contains a memory section for each split block in the L3 space:

.. code-block:: text

    // ----------------------- 256MB DMC0(DDR-A) ---------------------------------
    // 256MB DMC0 DDR SDRAM memory is partitioned as follows:
    //   DDR-A part1 : SHARC0 NW code, 3MB
    //   DDR-A part2 : SHARC1 NW code, 3MB
    //   DDR-A part3 : SHARC0 data, 4MB
    //   DDR-A part4 : SHARC0 VISA code, 3MB
    //   DDR-A part5 : SHARC1 VISA code, 3MB
    //   DDR-A part6 : SHARC0 data, 62MB
    //   DDR-A part7 : SHARC1 data, 66MB
    //   DDR-A part8 : ARM 112MB
    mem_DMC0_SDRAM_A1       { TYPE(BW RAM) START(0x80000000) END(0x802fffff) WIDTH(8) }
    mem_DMC0_SDRAM_A2       { TYPE(BW RAM) START(0x80300000) END(0x805fffff) WIDTH(8) }
    mem_DMC0_SDRAM_A3       { TYPE(BW RAM) START(0x80600000) END(0x809fffff) WIDTH(8) }
    mem_DMC0_SDRAM_A4       { TYPE(BW RAM) START(0x80a00000) END(0x80cfffff) WIDTH(8) }
    mem_DMC0_SDRAM_A5       { TYPE(BW RAM) START(0x80d00000) END(0x80ffffff) WIDTH(8) }
    mem_DMC0_SDRAM_A6       { TYPE(BW RAM) START(0x81000000) END(0x84dfffff) WIDTH(8) }
    mem_DMC0_SDRAM_A7       { TYPE(BW RAM) START(0x84e00000) END(0x88ffffff) WIDTH(8) }
    mem_DMC0_SDRAM_A8       { TYPE(BW RAM) START(0x89000000) END(0x8fffffff) WIDTH(8) }

    and a series of macros to map types of data to the appropriate sections:

   #define MY_SDRAM_NWCODE_MEM mem_DMC0_SDRAM_A1
   #define MY_SDRAM_DATA1_MEM  mem_DMC0_SDRAM_A3
   #define MY_SDRAM_DATA2_MEM  mem_DMC0_SDRAM_A6
   #define MY_SDRAM_SWCODE_MEM mem_DMC0_SDRAM_A4

To change the L3 space available to the SHARC, while preserving each of the sections, just update the start/end addresses for the applicable blocks.

To remove an L3 section available to the SHARC, remove the appropriate macro and either remove any output section referring to that macro or change the reference to another section.

To remove all of the L3 mapping from a SHARC, either uncheck the "**Use External Memory (SDRAM)**" box from the "**Startup Code/LDF**" tab in ``system.svc`` (generated LDF) or remove the ``USE_SDRAM`` macro from the linker options (non-generated LDF).

.. warning::

   Memory Protection: ARM and SHARC both have memory protection mechanisms. Be aware of this if you are re-allocating space in L2/L3 between the cores.

SHARCs and memory placement
============================

SHARCS use ADI proprietary .ldf (Linker description file) format to define memory layout and mapping. This is consumed by the linker and used to place all required code/data/metainformation into the executable. SHARC developers historically were very focused on memory placement. ARM takes an approach of "stick it all in L3".

Placing code and data on SHARC
-------------------------------

By default, symbols are mapped to location independent sections, which are linked into the appropriate L1 bank for that type (code, DM data or PM data). If that L1 block has insufficient space, the linker will silently spill it out to other L1 blocks, or L2 and finally L3 memory (if enabled). This means it'll be mapped to the best possible memory but there's no user control over the placement.

Specific section names are provided to force the linker to map a symbol to a specific area of memory. These can be accessed via pragmas in C/C++:

.. code-block:: c

   #pragma section("section")

Where section can be any of:

* **seg_int_code** - code to be mapped to internal (L1) memory
* **seg_ext_code** - code to be mapped to external (L3) memory
* **seg_int_data** - data to be mapped to internal (L1) memory
* **seg_ext_data** - data to be mapped to external (L3) memory
* **seg_l2** - code/data to be mapped to L2 memory

If any symbols cannot fit in the memory available to that section then the link will fail with an error and manual intervention is required to resolve the mapping. This generally involves mapping some symbols to a different section.

Soft placement is available via a **PrefersMem** attribute. This allows an object to express a preference to it's mapping location, of either internal, external or any. These can be defined via a pragma from C/C++:

.. code-block:: c

   #pragma file_attr ("prefersMem=internal")
   #pragma file_attr ("prefersMem=external")
   #pragma file_attr ("prefersMem=any")

or via a .file_attr statement from assembly:

.. code-block:: asm

   .file_attr prefersMem="internal";
   .file_attr prefersMem="external";
   .file_attr prefersMem="any";

The linker will map all of the symbols preferring '**internal**' before '**any**', and then finally '**external**'. This means that some preferences will not be satisfied but the symbols will be mapped anywhere space is available. There will be no linker notification if a symbol is mapped to a memory that wasn't requested. This can be used to de-prioritize symbols which are not performance critical, such as error handling, initialization or background code.

Enabling External Memory
------------------------

There's a checkbox in CCES project settings to use L3 (defined Linker macro ``USE_SDRAM``). Open **system.svc**, click on the **Startup Code/LDF** tab at the bottom of the window, the click the LDF tab on the left and check the box that says '**Use external memory (SDRAM)**'. You can then add it to the **Stacks and Heaps** table.

.. figure:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/1734950d-ce5f-44a5-8b0e-a8bc709a6350
   :alt: CCES External Memory Configuration

Adding an LDF to your project
-----------------------------

There are 3 options for LDFs within your project. Generated, non-generated and custom.

Generated LDFs
~~~~~~~~~~~~~~

These are the default option in CCES, using the "**Startup Code/LDF**" Add-In via the **system.svc** file. The generated LDFs will be regenerated when you change project settings (including part variants), and provides GUI controls to configure SDRAM memory size, stack and heap size/locations.

The generated LDFs contain VDSG comment sections at various points in the file, such as:

.. code-block:: text

   /*$VDSG<insert-user-libraries-at-beginning>                     */
   /* Text inserted between these $VDSG comments will be preserved */
   /*$VDSG<insert-user-libraries-at-beginning>                     */

These allow custom statements to be embedded in the LDF which will be preserved if the file is regenerated. Anything added outside of these comments will be lost. These sections allow the LDF to be customized with additional libraries or sections.

Non-generated LDF
~~~~~~~~~~~~~~~~~

Static LDF available from ``<CCES_INSTALL>/SHARC/ldf/<partname>.ldf`` These are configured via macros to determine stack/heap size and location, external memory size, etc. Details of the available macros are explained at the top of each file. This LDF is used if no LDF is provided within the project or on the command line using the ``-T`` switch.

Custom LDF
~~~~~~~~~~

Users can provide their own LDF files. Generally these are either copied from one of the ADI provided LDFs.

To use a custom version of the non-generated LDF, simply copy the applicable LDF into your project and update the Custom LDF (-T) field in the Linker settings.

To use a custom version of the generated LDF, you need to:

1. Create a copy of the ``system/startup_ldf`` directory from your project
2. Remove the "**Startup Code/LDF**" Add-In from the **system.svc** file
3. Update the Custom LDF (-T) field in the Linker settings

The additional files within the **startup_ldf** directory need to be kept in-step with the LDF file though none need to be changed when adjusting the memory allocation in the LDF.

Addressing data across the system
==================================

The ARM core shares the same address map as the system, so any ARM address can be passed to either SHARC core, DMA channel or peripheral as-is. The SHARC cores have an internal alias and a system alias for all of their L1 memory spaces. The internal alias allows for direct L1 access so should be used when accessing the memory from the SHARC, but needs to translate to the system address if that L1 space needs to be shared with another core (ARM or SHARC) or a peripheral. The SHARC toolchain provides APIs ``adi_rtl_internal_to_system_addr()`` and ``adi_rtl_system_to_internal_addr()`` to convert to/from system addresses.

Since the memory addresses of data may change due to memory placement at the link stage, it's recommended to pass addresses at run-time using MCAPI.