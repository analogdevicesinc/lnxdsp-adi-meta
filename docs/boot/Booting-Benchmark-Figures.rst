Introduction
============

The following are boot benchmark results that were obtained using a
logic analyzer. This was done by connecting to the SIGMA DSP (P3) port
on the carrier board. The logic analyzer being used was: Logic 8CH Hobby
Craft analyzer. The same is compatible with
`PulseView <https://sigrok.org/wiki/PulseView>`__, which works on
Windows and Linux.

Sigma Studio pin layouts
========================

The following are sigma studio pin layouts for the respective boards

ADSP-SC598 sigma studio pins
----------------------------

+---------+---------+---------+---------+---------+---------+---------+
| GPIO    | Name of | Ex      | sysfs   | U-boot  | Linux   | Note    |
|         | sch     | pansion | GPIO    | trace   | trace   |         |
|         | ematics | port    | number  |         |         |         |
|         |         | pin     |         |         |         |         |
+=========+=========+=========+=========+=========+=========+=========+
| PA_14   | T       | 1       | 14      |         |         | Uboot   |
|         | WI2_SCL |         |         |         |         | used by |
|         |         |         |         |         |         | i2c     |
+---------+---------+---------+---------+---------+---------+---------+
| PA_15   | T       | 3       | 15      |         |         | Uboot   |
|         | WI2_SDA |         |         |         |         | used by |
|         |         |         |         |         |         | i2c     |
+---------+---------+---------+---------+---------+---------+---------+
| PA_11   | SP      | 5       | 11      |         |         | Used by |
|         | I1_MISO |         |         |         |         | linux   |
|         |         |         |         |         |         | for     |
|         |         |         |         |         |         | spi     |
|         |         |         |         |         |         | 2_quad, |
|         |         |         |         |         |         | Free to |
|         |         |         |         |         |         | use in  |
|         |         |         |         |         |         | uboot   |
+---------+---------+---------+---------+---------+---------+---------+
| PA_10   | S       | 7       | 10      |         |         | Used by |
|         | PI1_CLK |         |         |         |         | linux   |
|         |         |         |         |         |         | for     |
|         |         |         |         |         |         | spi     |
|         |         |         |         |         |         | 2_quad, |
|         |         |         |         |         |         | Free to |
|         |         |         |         |         |         | use in  |
|         |         |         |         |         |         | uboot   |
+---------+---------+---------+---------+---------+---------+---------+
| PA_13   | SPI     | 9       | 13      |         |         | Uboot   |
|         | 1_SEL1B |         |         |         |         | used by |
|         |         |         |         |         |         | i2c     |
+---------+---------+---------+---------+---------+---------+---------+
| PA_12   | SP      | 8       | 12      |         |         | Used by |
|         | I1_MOSI |         |         |         |         | linux   |
|         |         |         |         |         |         | for     |
|         |         |         |         |         |         | spi     |
|         |         |         |         |         |         | 2_quad, |
|         |         |         |         |         |         | Free to |
|         |         |         |         |         |         | use in  |
|         |         |         |         |         |         | uboot   |
+---------+---------+---------+---------+---------+---------+---------+

ADSP-SC594 sigma studio pins
----------------------------

+---------+---------+---------+---------+---------+---------+---------+
| GPIO    | Name of | Ex      | sysfs   | U-boot  | Linux   | Note    |
|         | sch     | pansion | GPIO    | trace   | trace   |         |
|         | ematics | port    | number  |         |         |         |
|         |         | pin     |         |         |         |         |
+=========+=========+=========+=========+=========+=========+=========+
| PA_14   | T       | 1       | 14      |         |         | Uboot   |
|         | WI2_SCL |         |         |         |         | used by |
|         |         |         |         |         |         | i2c     |
+---------+---------+---------+---------+---------+---------+---------+
| PA_15   | T       | 3       | 15      |         |         | Uboot   |
|         | WI2_SDA |         |         |         |         | used by |
|         |         |         |         |         |         | i2c     |
+---------+---------+---------+---------+---------+---------+---------+
| PA_11   | SP      | 5       | 11      |         |         | Used by |
|         | I1_MISO |         |         |         |         | linux   |
|         |         |         |         |         |         | for     |
|         |         |         |         |         |         | spi     |
|         |         |         |         |         |         | 2_quad, |
|         |         |         |         |         |         | Free to |
|         |         |         |         |         |         | use in  |
|         |         |         |         |         |         | uboot   |
+---------+---------+---------+---------+---------+---------+---------+
| PA_10   | S       | 7       | 10      |         |         | Used by |
|         | PI1_CLK |         |         |         |         | linux   |
|         |         |         |         |         |         | for     |
|         |         |         |         |         |         | spi     |
|         |         |         |         |         |         | 2_quad, |
|         |         |         |         |         |         | Free to |
|         |         |         |         |         |         | use in  |
|         |         |         |         |         |         | uboot   |
+---------+---------+---------+---------+---------+---------+---------+
| PA_13   | SPI     | 9       | 13      |         |         | Uboot   |
|         | 1_SEL1B |         |         |         |         | used by |
|         |         |         |         |         |         | i2c     |
+---------+---------+---------+---------+---------+---------+---------+
| PA_12   | SP      | 8       | 12      |         |         | Used by |
|         | I1_MOSI |         |         |         |         | linux   |
|         |         |         |         |         |         | for     |
|         |         |         |         |         |         | spi     |
|         |         |         |         |         |         | 2_quad, |
|         |         |         |         |         |         | Free to |
|         |         |         |         |         |         | use in  |
|         |         |         |         |         |         | uboot   |
+---------+---------+---------+---------+---------+---------+---------+

Linux boot benchmark results
============================

ADSP-SC598
----------

+-------------+-------------+-------------+-------------+-------------+
| PIN         | Start       | Stop        | Time        | Note        |
+=============+=============+=============+=============+=============+
| PA12        | Start board | Board       | **0.35      |             |
|             | init        | ini         | sec**       |             |
|             | ialisiation | tialisation |             |             |
+-------------+-------------+-------------+-------------+-------------+
| PA10        | Start       | Stop        |             | SC598       |
|             | un          | un          |             | kernel is   |
|             | compressing | compressing |             | u           |
|             | kernel      | kernel      |             | ncompressed |
|             |             |             |             | uboot       |
|             |             |             |             | stage, not  |
|             |             |             |             | aligned to  |
|             |             |             |             | SC594       |
+-------------+-------------+-------------+-------------+-------------+
| PA11        | Jump to     | Linux       | **11 sec**  | No falcon   |
|             | Linux       | initializes |             | boot 3      |
|             |             | peripherals |             | seconds     |
|             |             |             |             | waiting for |
|             |             |             |             | command     |
|             |             |             |             | prompt      |
+-------------+-------------+-------------+-------------+-------------+
| PA12        | Systemd     |             | **31 sec**  |             |
|             | triggers    |             |             |             |
|             | GPIO in     |             |             |             |
|             | sysfs       |             |             |             |
+-------------+-------------+-------------+-------------+-------------+

ADSP-SC594
----------

+-------------+-------------+-------------+-------------+-------------+
| PIN         | Start       | Stop        | Time        | Note        |
+=============+=============+=============+=============+=============+
| PA12        | Start       | Stop        | **0.010     |             |
|             | loading SPI | loading SPI | sec**       |             |
|             | fitImage    | fitImage    |             |             |
+-------------+-------------+-------------+-------------+-------------+
| PA10        | Start       | Stop        | **4 sec**   |             |
|             | un          | un          |             |             |
|             | compressing | compressing |             |             |
|             | kernel      | kernel      |             |             |
+-------------+-------------+-------------+-------------+-------------+
| PA11        | Jump to     | Linux       | **2.6 sec** |             |
|             | Linux       | initializes |             |             |
|             |             | peripherals |             |             |
+-------------+-------------+-------------+-------------+-------------+
| PA12        | Systemd     |             | **23 sec**  |             |
|             | triggers    |             |             |             |
|             | GPIO in     |             |             |             |
|             | sysfs       |             |             |             |
+-------------+-------------+-------------+-------------+-------------+

ADSP-SC594 (falcon mode)
------------------------

Kernel start uncompressing 4 seconds eaerlier PIN \| Start \| Stop \|
Time \| Note – \| – \| – \| – \| – PA12 \| Start loading SPI fitImage \|
Stop loading SPI fitImage \| **0.010 sec** \|   PA10 \| Start
uncompressing kernel \| Stop uncompressing kernel \| **~1 sec** \| Cant
see on the logic analyzer, maybe taking different code path PA11 \| Jump
to Linux \| Linux initializes peripherals \| **2.5 sec** \|   PA12 \|
Systemd triggers GPIO in sysfs \|   \| **23 sec** \| 23 sec after kernel
configures peripherals
