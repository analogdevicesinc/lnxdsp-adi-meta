===================
Test Report (3.1.0)
===================



.. note::

   This test report applies to versions 3.1.x. Any regressions specific to a .x release will be explicitly noted



.. list-table::
   :header-rows: 1

   * - Testing Component
     - SC598-SOM-EZKIT
     - SC594-SOM-EZKIT
     - SC589-EZKIT
     - SC589-MINI
     - SC584-EZKIT
     - SC573-EZKIT
   * - **SRU** *DAI routing configuration*
     - 
     - 
     - 
     - 
     - 
     - 
   * - **SPORT** *sending I2S data to CODECs*
     - 
     - 
     - 
     - 
     - 
     - 
   * - **ADAU1761** *Audio In Driver*
     - 
     - 
     - 
     - 
     - 
     - 
   * - **ADAU1962** *Audio Out Driver*
     - ✅
     - ✅
     - ✅
     - ✅
     - 
     - ✅
   * - **ADAU1979** *Audio In Driver*
     - 
     - 
     - 
     - 
     - 
     - 
   * - **ICAP**
     - 
     - 
     - 
     - 
     - 
     - 
   * - **USB Gadget Audio**
     - ✅
     - ✅
     - 
     - 
     - 
     - 
   * - **SHARC ALSA**
     - 
     - 
     - 
     - 
     - 
     - 
   * - **remoteproc**
     - ✅
     - ✅
     - 
     - ✅
     - 
     - 
   * - **virtio rpmsg**
     - ✅
     - ✅
     - 
     - ✅
     - 
     - 
   * - **Common Clock Framework**
     - ✅
     - ✅
     - ✅
     - 
     - ✅
     - ✅
   * - **Crypto Framework**
     - ➖
     - ➖
     - **N/A**
     - **N/A**
     - **N/A**
     - **N/A**
   * - **DMA Peripheral** *Implicitly tested as it's used by SPI, OSPI, and UART drivers*
     - ✅
     - ✅
     - 
     - ✅
     - 
     - 
   * - **MDMA** *Implicitly tested as it's used only by remoteproc to load data to SHARC shared memory*
     - ✅
     - ✅
     - 
     - ✅
     - 
     - 
   * - **EMAC0 (eth0)**
     - ✅
     - ✅
     - ✅
     - ✅
     - ✅
     - ✅
   * - **EMAC0 (AVB)**
     - 
     - 
     - 
     - 
     - 
     - 
   * - **EMAC1** :sup:`1`
     - ➖
     - ➖
     - ➖
     - ➖
     - ➖
     - ➖
   * - **GPIO Control**
     - 
     - 
     - 
     - 
     - 
     - 
   * - **GPTimers**
     - ✅
     - ✅
     - ✅
     - ✅
     - 
     - ✅
   * - **I2C**
     - ✅
     - ✅
     - ✅
     - ✅
     - 
     - ✅
   * - **SRAM/L2 alloc**
     - ✅
     - ✅
     - ✅
     - ✅
     - 
     - 
   * - **Pin Control Driver**
     - 
     - 
     - 
     - 
     - 
     - 
   * - **RCU driver/system reset**
     - 
     - 
     - 
     - 
     - 
     - 
   * - **SPI**
     - ✅
     - ✅
     - ✅
     - ✅
     - ✅
     - ✅
   * - **QSPI Boot**
     - ✅
     - ✅
     - ✅
     - ✅
     - ✅
     - ✅
   * - **eMMC** ``dev/mmcblk0``
     - ➖
     - **N/A**
     - **N/A**
     - **N/A**
     - **N/A**
     - **N/A**
   * - **eMMC Boot** :sup:`3`
     - ➖
     - **N/A**
     - **N/A**
     - **N/A**
     - **N/A**
     - **N/A**
   * - **SD** *dev/mmcblk0*
     - ✅
     - **N/A**
     - ✅
     - ✅
     - **N/A**
     - ✅
   * - **SD Boot**
     - ✅
     - **N/A**
     - ✅
     - ✅
     - **N/A**
     - ✅
   * - **OSPI** :sup:`4`
     - **N/A**
     - ✅
     - **N/A**
     - **N/A**
     - **N/A**
     - **N/A**
   * - **uart0**
     - ✅
     - ✅
     - ✅
     - ✅
     - ✅
     - ✅
   * - **USB: Host**
     - ✅
     - ✅
     - ✅
     - ✅
     - ✅
     - ✅
   * - **USB: Device**
     - ✅
     - ✅
     - ✅
     - ✅
     - ✅
     - ✅
   * - **Watchdog**
     - 
     - 
     - 
     - 
     - 
     - 


| ✅: PASS
| ❗: PASS with warning(s), see note
| **N/A**: Not Applicable
| ➖:  Not Tested

---------------------------------------------------------

**Notes:**

1. Currently, the driver can enumerate the IP and identify what is present, but the PHY is not configured/reset yet and so ethernet communications do not work.

2. There is a known issue on the SC589-MINI's watchdog which prevents the RCU driver/system from resetting properly

3. Currently, booting with the kernel and RFS on SD/eMMC is the only supported configuration, primarily due to hardware limitations on the SOM-CRR-EZKIT (so U-Boot must still be stored on the SPI flash)

4. OSPI is not currently working for MX66 chip under 5.15 kernel. OSPI (1S-8S-8S mode) does work for SC594 & IS25LX256
