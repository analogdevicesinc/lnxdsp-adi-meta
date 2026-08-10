Booting Benchmark Figures
=========================

Introduction
------------

Boot benchmark results were obtained using a logic analyzer connected to the Sigma DSP (P3) port on the carrier board. The measurements were captured using a Logic 8CH Hobby Craft analyzer, compatible with `PulseView <https://sigrok.org/wiki/PulseView>`_, which runs on Windows and Linux.

Test Setup
----------

Logic Analyzer Configuration
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The logic analyzer was connected to specific GPIO pins on the carrier board's expansion port (Sigma DSP P3 connector). These pins were toggled at key boot milestones to measure timing.

ADSP-SC598 Pin Configuration
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. list-table::
   :header-rows: 1
   :widths: 10 15 10 10 12 12 31

   * - GPIO
     - Schematic Name
     - Expansion Port Pin
     - sysfs GPIO Number
     - U-Boot Trace
     - Linux Trace
     - Notes
   * - PA_14
     - TWI2_SCL
     - 1
     - 14
     -
     -
     - U-Boot used by I2C
   * - PA_15
     - TWI2_SDA
     - 3
     - 15
     -
     -
     - U-Boot used by I2C
   * - PA_11
     - SPI1_MISO
     - 5
     - 11
     -
     -
     - Linux used for SPI 2_quad, free in U-Boot
   * - PA_10
     - SPI1_CLK
     - 7
     - 10
     -
     -
     - Linux used for SPI 2_quad, free in U-Boot
   * - PA_13
     - SPI1_SEL1B
     - 9
     - 13
     -
     -
     - U-Boot used by I2C
   * - PA_12
     - SPI1_MOSI
     - 8
     - 12
     -
     -
     - Linux used for SPI 2_quad, free in U-Boot

ADSP-SC594 Pin Configuration
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. list-table::
   :header-rows: 1
   :widths: 10 15 10 10 12 12 31

   * - GPIO
     - Schematic Name
     - Expansion Port Pin
     - sysfs GPIO Number
     - U-Boot Trace
     - Linux Trace
     - Notes
   * - PA_14
     - TWI2_SCL
     - 1
     - 14
     -
     -
     - U-Boot used by I2C
   * - PA_15
     - TWI2_SDA
     - 3
     - 15
     -
     -
     - U-Boot used by I2C
   * - PA_11
     - SPI1_MISO
     - 5
     - 11
     -
     -
     - Linux used for SPI 2_quad, free in U-Boot
   * - PA_10
     - SPI1_CLK
     - 7
     - 10
     -
     -
     - Linux used for SPI 2_quad, free in U-Boot
   * - PA_13
     - SPI1_SEL1B
     - 9
     - 13
     -
     -
     - U-Boot used by I2C
   * - PA_12
     - SPI1_MOSI
     - 8
     - 12
     -
     -
     - Linux used for SPI 2_quad, free in U-Boot

Benchmark Results
-----------------

ADSP-SC598
~~~~~~~~~~

.. list-table::
   :header-rows: 1
   :widths: 15 25 25 15 20

   * - PIN
     - Start
     - Stop
     - Time
     - Notes
   * - PA12
     - Start board initialization
     - Board initialization complete
     - **0.35 sec**
     -
   * - PA10
     - Start uncompressing kernel
     - Stop uncompressing kernel
     -
     - SC598 kernel is uncompressed at U-Boot stage, not aligned to SC594
   * - PA11
     - Jump to Linux
     - Linux initializes peripherals
     - **11 sec**
     - No falcon boot; 3 seconds waiting for command prompt
   * - PA12
     - Systemd triggers GPIO in sysfs
     -
     - **31 sec**
     -

ADSP-SC594
~~~~~~~~~~

.. list-table::
   :header-rows: 1
   :widths: 15 25 25 15 20

   * - PIN
     - Start
     - Stop
     - Time
     - Notes
   * - PA12
     - Start loading SPI fitImage
     - Stop loading SPI fitImage
     - **0.010 sec**
     -
   * - PA10
     - Start uncompressing kernel
     - Stop uncompressing kernel
     - **4 sec**
     -
   * - PA11
     - Jump to Linux
     - Linux initializes peripherals
     - **2.6 sec**
     -
   * - PA12
     - Systemd triggers GPIO in sysfs
     -
     - **23 sec**
     -

ADSP-SC594 (Falcon Mode)
~~~~~~~~~~~~~~~~~~~~~~~~~

.. note::

   Kernel starts uncompressing 4 seconds earlier with Falcon Mode enabled.

.. list-table::
   :header-rows: 1
   :widths: 15 25 25 15 30

   * - PIN
     - Start
     - Stop
     - Time
     - Notes
   * - PA12
     - Start loading SPI fitImage
     - Stop loading SPI fitImage
     - **0.010 sec**
     -
   * - PA10
     - Start uncompressing kernel
     - Stop uncompressing kernel
     - **~1 sec**
     - Cannot see on logic analyzer, possibly taking different code path
   * - PA11
     - Jump to Linux
     - Linux initializes peripherals
     - **2.5 sec**
     -
   * - PA12
     - Systemd triggers GPIO in sysfs
     -
     - **23 sec**
     - 23 sec after kernel configures peripherals
