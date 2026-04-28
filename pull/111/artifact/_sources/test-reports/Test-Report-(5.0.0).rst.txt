===================
Test Report (5.0.0)
===================



.. note::

   This test report applies to version 5.0.x. Any regressions specific to a .x release will be explicitly noted



.. list-table::
   :header-rows: 1

   * - Testing Component
     - SC598-SOM-EZKIT
     - SC594-SOM-EZKIT
     - SC589-MINI
     - SC573-EZKIT
   * - **ADAU1962** *Audio Out Driver*
     - ✅
     - ✅
     - ✅
     - ✅
   * - **USB Gadget Audio**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **SHARC ALSA**
     - ✅
     - **N/A**
     - **N/A**
     - **N/A**
   * - **remoteproc**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **virtio rpmsg**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **Common Clock Framework**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **Crypto Framework**
     - ➖
     - ➖
     - **N/A**
     - **N/A**
   * - **DMA Peripheral** *Implicitly tested as it's used by SPI, OSPI, and UART drivers*
     - ✅
     - ✅
     - ✅
     - ✅
   * - **MDMA** *Implicitly tested as it's used only by remoteproc to load data to SHARC shared memory*
     - ✅
     - ✅
     - ✅
     - ✅
   * - **EMAC0 (eth0)**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **EMAC1** :sup:`1`
     - ➖
     - ➖
     - ➖
     - ➖
   * - **GPTimers**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **I2C**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **SRAM/L2 alloc**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **SPI**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **QSPI Boot**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **eMMC** ``dev/mmcblk0``
     - ➖
     - **N/A**
     - **N/A**
     - **N/A**
   * - **eMMC Boot** :sup:`2`
     - ➖
     - **N/A**
     - **N/A**
     - **N/A**
   * - **SD** *dev/mmcblk0*
     - ✅
     - **N/A**
     - ✅
     - ✅
   * - **SD Boot**
     - ✅
     - **N/A**
     - ✅
     - ✅
   * - **OSPI** :sup:`3`
     - **N/A**
     - ✅
     - **N/A**
     - **N/A**
   * - **uart0**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **USB: Host**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **USB: Device**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **Watchdog**
     - ✅
     - ✅
     - ✅
     - ✅


| ✅: PASS
| ❗: PASS with warning(s), see note
| ❌: FAIL, see note
| **N/A**: Not Applicable
| ➖:  Not Tested

---------------------------------------------------------

**Notes:**

1. Currently, the driver can enumerate the IP and identify what is present, but the PHY is not configured/reset yet and so ethernet communications do not work.

2. Currently, booting with the kernel and RFS on SD/eMMC is the only supported configuration, primarily due to hardware limitations on the SOM-CRR-EZKIT (so U-Boot must still be stored on the SPI flash)

3. OSPI is not currently working for MX66 chip under 5.x and 6.x kernel versions. OSPI (1S-8S-8S mode) does work for SC594 & IS25LX256
