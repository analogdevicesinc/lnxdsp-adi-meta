============
RPMsg-Lite
============

Introduction
============

This guide helps you implement RPMsg-Lite for bare-metal applications on ADSP-SC5xx processors. RPMsg-Lite is a lightweight implementation of the RPMsg protocol designed specifically for bare-metal and RTOS environments where the full Linux RPMsg stack is not available.

While Linux on the ARM core includes native RPMsg support through kernel drivers, your SHARC+ DSP cores and bare-metal ARM applications need a different approach. RPMsg-Lite provides this solution—a minimal, efficient implementation that enables inter-processor communication without requiring a full operating system.

**What you'll learn:**

* System resources used by RPMsg-Lite (shared memory, interrupts, TRU triggers)
* How to allocate and configure memory for communication buffers
* Creating resource tables for cross-processor coordination
* Setting up RPMsg-Lite instances, endpoints, and message transmission
* Practical implementation differences between ARM and SHARC+ cores

**Prerequisites:**

* Understanding of RPMsg concepts—see :doc:`What is RPMsg? <RPMsg>`
* Familiarity with bare-metal or RTOS development
* ADSP-SC5xx development environment configured

**Additional resources:**

* `RPMsg-Lite Repository <https://github.com/analogdevicesinc/rpmsg-lite>`_
* `RPMsg Examples <https://github.com/analogdevicesinc/lnxdsp-examples>`_

Overview
========

RPMsg-Lite enables message transfer between cores on ADSP devices. An implementation is available for ARM when running Linux, and for bare-metal applications, a port of RPMsg-Lite is available for ARM and SHARC+ cores. It allows for transmitting a message to a specific endpoint on a different core via a dedicated transport link. Multiple endpoints can be registered against a single link.

Resources used
--------------

In order to allow for message transmission between cores on ADSP-SC5xx devices, RPMsg-Lite makes use of the following resources:

* Shared memory for storing message buffers and vring buffers containing message descriptors
* Interrupt on each core to indicate that a message has arrived
* TRU triggers for raising an interrupt on a different core

.. note::

   These resources should not be used for other purposes when using RPMsg-Lite in your application.

The interrupts used are:

.. code-block:: text

   TRGS_TRU0_IRQ3      /* ARM */
   TRGS_TRU0_IRQ7      /* SHARC0 */
   TRGS_TRU0_IRQ11     /* SHARC1 */

The TRU triggers allocated are:

.. code-block:: text

   TRGM_SOFT3          /* Signal ARM */
   TRGM_SOFT4          /* Signal SHARC0 */
   TRGM_SOFT5          /* Signal SHARC1 */

The shared memory used is allocated by a single core and typically by the main core in an RPMsg-Lite context. In order for the remotes to know the physical addresses for the vring buffers in shared memory, this information has to be passed from the main to the remote before the framework can be used. This is achieved by the main populating a resource table at a known address. The examples on this pages utilise memory located at ``___MCAPI_common_start``. If using a different location for this make sure that the memory area used is not cached.

For the sake of compatibility, the resource table used in the examples has an identical structure to the one created by ``remoteproc`` in Linux, which looks as follows:

.. code-block:: c

   struct sharc_resource_table {
       struct resource_table table_hdr;
       unsigned int offset[1];
       struct fw_rsc_vdev rpmsg_vdev;
       struct fw_rsc_vdev_vring vring[2];
   };

   struct adi_resource_table {
       uint8_t tag[16];
       uint32_t version;
       uint32_t initialized;
       uint32_t reserved[8];
       struct sharc_resource_table tbl;
   };

.. note::

   Additional details can be found on `remoteproc.h (RPMsg-Lite repo) <https://github.com/analogdevicesinc/rpmsg-lite/blob/adi/release/yocto-2.1.0/lib/include/remoteproc.h>`_

Using RPMsg-Lite on SC5xx
=========================

Allocate memory for vring buffers
----------------------------------

The memory allocation for for the vring buffers should be done on a single core and the location of those buffers shared with the other cores via a resource table. To ensure that the cores pick up the data from the shared memory, it is necessary to mark the memory as  not cached on all cores using the buffers. Allocating memory and ensuring it is not cached is done differently on ARM and SHARC. If using RPMsg-Lite on the ARM it is recommended that the memory is allocated on the ARM as the SHARCs allow for simple run-time changes to caching via a set of registers.

Create an array for the vring buffers on the heap.

.. code-block:: c

   uint8_t vring_buffer[ADI_VRING_BUFFER_SIZE];

Disable cache on ARM
~~~~~~~~~~~~~~~~~~~~

Disabling cache on the ARM for the vring buffers requires changes to the linker files. For example, to allocate 4MB of L3 memory as not cached memory on an SC594:

1. Modify ``apt.c``

   Add a definition for an uncached block of memory and adjust the block of memory from which it is taken from. Change:

   .. code-block:: c

      { 0xA0000000u, 0xBFFFFFFFu, ADI_MMU_WB_CACHED           }, /* 512MB DDR-A */

   To:

   .. code-block:: c

      { 0xA0000000u, 0xA03FFFFFu, ADI_MMU_RW_UNCACHED         }, /* 4MB DDR-A */
      { 0xA0400000u, 0xBFFFFFFFu, ADI_MMU_WB_CACHED           }, /* 508MB DDR-A */

2. Modify ``app.ld``

   Add a definition for a non-cached block of memory and adjust the block of memory from which it is taken from, matching the changes made to ``apt.c``. Change:

   .. code-block:: text

      /* ARM Core 0 L3, DMC0 */
      MEM_L3 : ORIGIN = 0xA0000000, LENGTH = 512M

   To:

   .. code-block:: text

      /* ARM Core 0 L3, DMC0 */
      MEM_L3_UNCACHED : ORIGIN = 0xA0000000, LENGTH = 4M
      MEM_L3 : ORIGIN = 0xA0400000, LENGTH = 508M

3. Add an entry in the ``SECTIONS`` covering L3 memory

   .. code-block:: text

      /* L3 Uncached Memory. No data placed here by default */
      .l3_uncached :
      {
        *(.l3_data_uncached)
      } >MEM_L3_UNCACHED = 0

   To store the vring buffers previously declared in the non-cached memory block, use ``__attribute__`` to specify the memory section it should be mapped to.

   .. code-block:: c

      __attribute__ ((section (".l3_data_uncached")))
      uint8_t vring_buffer[ADI_VRING_BUFFER_SIZE];

Disable cache on SHARC
~~~~~~~~~~~~~~~~~~~~~~

The SHARCs have a number of range registers allowing for sections of memory to be marked as non-cached. We make use of these to mark the descriptor buffer range and the message buffer range as not cached like in the following example

.. code-block:: c

   	// Disable cache for the descriptors memory range
   	status = adi_cache_set_range ((void *)vring_buffer,
   					(void *)(vring_buffer + ADI_VRING_BUFFER_SIZE),
   					adi_cache_rr6,
   					adi_cache_noncacheable_range);

If the SHARC is acting as an RPMsg-Lite remote the ranges to mark as non-cached are retrieved from the resource table.

Create Resource Table
---------------------

On the core on which the memory for the vring buffers was declared, create the resource table used to provide information on the shared memory resources used for RPMsg-lite and instruct the linker to store it at a fixed location in L2 memory, which is by default marked as non-cached.

.. code-block:: c

   RL_PACKED_BEGIN
   struct sharc_resource_table {
   	struct resource_table table_hdr;
   	unsigned int offset[1];
   	struct fw_rsc_vdev rpmsg_vdev;
   	struct fw_rsc_vdev_vring vring[2];
   }RL_PACKED_END;

   RL_PACKED_BEGIN
   struct adi_resource_table {
   	uint8_t tag[16];
   	uint32_t version;
   	uint32_t initialized;
   	uint32_t reserved[8];

   	struct sharc_resource_table tbl;
   }RL_PACKED_END;

   const struct adi_resource_table rsc_tbl_local = {
   		.tag = "AD-RESOURCE-TBL",
   		.version = 1,
   		.initialized = 0,
   		.tbl.table_hdr = {
   			/* resource table header */
   			1, 			/* version */
   			1, 			/* number of table entries */
   			{0, 0,},	/* reserved fields */
   		},
   		.tbl.offset = {offsetof(struct sharc_resource_table, rpmsg_vdev),
   		},
   		.tbl.rpmsg_vdev = {RSC_VDEV, /* virtio dev type */
   			7, 				/* it's rpmsg virtio */
   			1, 				/* kick sharc0 */
   			/* 1<<0 is VIRTIO_RPMSG_F_NS bit defined in virtio_rpmsg_bus.c */
   			1<<0, 0, 0, 0,	/* dfeatures, gfeatures, config len, status */
   			2, 				/* num_of_vrings */
   			{0, 0,}, 		/* reserved */
   		},
   		.tbl.vring = {
   			{(uint32_t)-1, VRING_ALIGN, RL_BUFFER_COUNT, 1, 0}, /* da allocated by rpmsg_lite_master_init */
   			{(uint32_t)-1, VRING_ALIGN, RL_BUFFER_COUNT, 1, 0}, /* da allocated by rpmsg_lite_master_init */
   		},
   };

   /*
    * Declare the resource table used for sharing shmem vring details.
    * The ___MCAPI_common_start address is defined in app.ldf
    */
   extern struct adi_resource_table __MCAPI_common_start;
   volatile struct adi_resource_table *adi_resource_table;
   volatile struct sharc_resource_table *resource_table;

Adding an rpmsg-lite instance
------------------------------

.. note::

   Currently the rpmsg-lite port for ADSP devices only supports static context instances.

1. Create a header file named "``rpmsg_config.h``" and define ``RL_USE_STATIC_API``. RPMsg-Lite will attempt to include this header file during build.

   .. code-block:: c

      #define RL_USE_STATIC_API (1)

2. Create the static RPMsg-Lite instance.

   .. code-block:: c

      struct rpmsg_lite_instance rpmsg_SHARC_channel;

   The instance makes use of a Link ID to determine which core the instance should connect to.

   .. code-block:: c

      /* Link IDs to use on ARM core */
      #define RL_PLATFORM_ARM_SHARC0_LINK_ID (0U)  /* Link between ARM and SHARC0 */
      #define RL_PLATFORM_ARM_SHARC1_LINK_ID (1U)  /* Link between ARM and SHARC1 */

      /* Link IDs to use on a SHARC core */
      #define RL_PLATFORM_SHARC_ARM_LINK_ID (0U)   /* Link between SHARC and ARM */
      #define RL_PLATFORM_SHARC_SHARC_LINK_ID (1U) /* Link between SHARC and SHARC */

3. Initialize the RPMsg-Lite instance on the main core

   .. code-block:: c

      rpmsg_instance = rpmsg_lite_master_init(
              (void*)vring_buffer,
              ADI_VRING_BUFFER_SIZE,
              RL_PLATFORM_ARM_SHARC0_LINK_ID,
              RL_SHM_VDEV,
              &rpmsg_SHARC_channel);

   Populate the resource table with the addresses of the vrings created by rpmsg_lite_master_init and signal the remote core that the resource table has been initialised

   .. code-block:: c

      adi_resource_table = (struct adi_resource_table *)
          ((uint32_t)&__MCAPI_common_start);
      memcpy((void*)adi_resource_table, (const void*)&rsc_tbl_local, sizeof(struct adi_resource_table));

      resource_table = &adi_resource_table->tbl;
      resource_table->vring[0].da = (uint32_t) rpmsg_instance->rvq->vq_ring_mem;
      resource_table->vring[1].da = (uint32_t) rpmsg_instance->tvq->vq_ring_mem;
      resource_table->rpmsg_vdev.status = 7;

   On the remote side the instance is initialized as follows

   .. code-block:: c

      rpmsg_instance = rpmsg_lite_remote_init(
              (void*)&resource_table_arm->rpmsg_vdev,
              RL_PLATFORM_SHARC_ARM_LINK_ID,
              RL_SHM_VDEV,
              &rpmsg_ARM_channel);

Adding an endpoint
~~~~~~~~~~~~~~~~~~

A unique endpoint is determined by a ``uint32`` address.

1. Create the static context for the endpoint.

   .. code-block:: c

      struct rpmsg_lite_ept_static_context sharc_ping_endpoint_context;

2. Create a callback function for handling messages received for the endpoint

   .. code-block:: c

      int32_t sharc_ping_call_back(void *payload, uint32_t payload_len, uint32_t src, void *priv) {
      	printf("Received a ping from SHARC0\n");
      	return RL_RELEASE;
      }

3. Create an endpoint

   .. code-block:: c

      uint32_t addr = ARM_EP_ADDR;

      rpmsg_ept = rpmsg_lite_create_ept(
              &rpmsg_SHARC_channel,
              addr,
              &sharc_ping_call_back,
              NULL,
              &sharc_ping_endpoint_context);

Sending a message
~~~~~~~~~~~~~~~~~

In order to send a message the recipient endpoint address must be known.

.. code-block:: c

   uint32_t remote_addr = SHARC0_EP_ADDR;
   char msg[] = "ping";
   uint32_t len = 4;

   return rpmsg_lite_send(
   		&rpmsg_SHARC_channel,
   		&sharc_ping_endpoint_context.ept,
   		remote_addr,
   		msg, len, 0);

Additional Information
======================

Downloads
---------

RPMsg-Lite for ADSP devices is available for Bare-Metal applications running on SHARC+, ARM A5 and ARM A55 on the `RPMsg-Lite repo <https://github.com/analogdevicesinc/rpmsg-lite>`_. To guarantee compatibility, use the same version of RPMsg-Lite on all cores. If running Linux on the ARM use the RPMsg-Lite version matching the Linux for ADSP-SC5xx release version running on the ARM.

Examples
--------

`lnxdsp-examples repo <https://github.com/analogdevicesinc/lnxdsp-examples>`_
