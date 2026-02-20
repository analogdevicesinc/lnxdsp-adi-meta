# Introduction
The following are boot benchmark results that were obtained using a logic analyzer. 
This was done by connecting to the SIGMA DSP (P3) port on the carrier board. The logic analyzer being used was: Logic 8CH Hobby Craft analyzer. The same is compatible with [PulseView](https://sigrok.org/wiki/PulseView), which works on Windows and Linux.

<img src="https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/141642367/6672240f-bbb5-4ebd-a508-32640ee29525" width="500">

# Sigma Studio pin layouts
The following are sigma studio pin layouts for the respective boards

## ADSP-SC598 sigma studio pins
GPIO | Name of schematics | Expansion port pin | sysfs GPIO number | U-boot trace | Linux trace | Note
-- | -- | -- | -- | -- | -- | --
PA_14 | TWI2_SCL | 1 | 14 |   |   | Uboot used by i2c
PA_15 | TWI2_SDA | 3 | 15 |   |   | Uboot used by i2c
PA_11 | SPI1_MISO | 5 | 11 |   |   | Used by linux for spi2_quad, Free to use in uboot
PA_10 | SPI1_CLK | 7 | 10 |   |   | Used by linux for spi2_quad, Free to use in uboot
PA_13 | SPI1_SEL1B | 9 | 13 |   |   | Uboot used by i2c
PA_12 | SPI1_MOSI | 8 | 12 |   |   | Used by linux for spi2_quad, Free to use in uboot


## ADSP-SC594 sigma studio pins
GPIO | Name of schematics | Expansion port pin | sysfs GPIO number | U-boot trace | Linux trace | Note
-- | -- | -- | -- | -- | -- | --
PA_14 | TWI2_SCL | 1 | 14 |   |   | Uboot used by i2c
PA_15 | TWI2_SDA | 3 | 15 |   |   | Uboot used by i2c
PA_11 | SPI1_MISO | 5 | 11 |   |   | Used by linux for spi2_quad, Free to use in uboot
PA_10 | SPI1_CLK | 7 | 10 |   |   | Used by linux for spi2_quad, Free to use in uboot
PA_13 | SPI1_SEL1B | 9 | 13 |   |   | Uboot used by i2c
PA_12 | SPI1_MOSI | 8 | 12 |   |   | Used by linux for spi2_quad, Free to use in uboot

# Linux boot benchmark results
## ADSP-SC598
PIN | Start | Stop | Time | Note
-- | -- | -- | -- | --
PA12 | Start board initialisiation | Board initialisation | **0.35 sec** |  
PA10 | Start uncompressing kernel | Stop uncompressing kernel |   | SC598 kernel is uncompressed uboot stage, not aligned to SC594
PA11 | Jump to Linux | Linux initializes peripherals | **11 sec** | No falcon boot 3 seconds waiting for command prompt
PA12 | Systemd triggers GPIO in sysfs |   | **31 sec** |  

## ADSP-SC594
PIN | Start | Stop | Time | Note
-- | -- | -- | -- | --
PA12 | Start loading SPI fitImage | Stop loading SPI fitImage | **0.010 sec** |  
PA10 | Start uncompressing kernel | Stop uncompressing kernel | **4 sec** |  
PA11 | Jump to Linux | Linux initializes peripherals | **2.6 sec** |  
PA12 | Systemd triggers GPIO in sysfs |   | **23 sec** |  


## ADSP-SC594 (falcon mode)
Kernel start uncompressing 4 seconds eaerlier
PIN | Start | Stop | Time | Note
-- | -- | -- | -- | --
PA12 | Start loading SPI fitImage | Stop loading SPI fitImage | **0.010 sec** |  
PA10 | Start uncompressing kernel | Stop uncompressing kernel | **~1 sec** | Cant see on the logic analyzer, maybe taking different code path
PA11 | Jump to Linux | Linux initializes peripherals | **2.5 sec** |  
PA12 | Systemd triggers GPIO in sysfs |   | **23 sec** | 23 sec after kernel configures peripherals



