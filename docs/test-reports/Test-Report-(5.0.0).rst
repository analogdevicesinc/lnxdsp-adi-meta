===================
Test Report (5.0.0)
===================



.. list-table::
   :header-rows: 1

   * - :memo:
     - This test report applies to version 5.0.x. Any regressions specific to a .x release will be explicitly noted



.. list-table::
   :header-rows: 1

   * - Testing Component
     - SC598-SOM-EZKIT
     - SC594-SOM-EZKIT
     - SC589-MINI
     - SC573-EZKIT
   * - **ADAU1962** _Audio Out Driver_
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
     - __N/A__
     - __N/A__
     - __N/A__
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
     - __N/A__
     - __N/A__
   * - **DMA Peripheral** _Implicitly tested as it's used by SPI, OSPI, and UART drivers_
     - ✅
     - ✅
     - ✅
     - ✅
   * - **MDMA** _Implicitly tested as it's used only by remoteproc to load data to SHARC shared memory_
     - ✅
     - ✅
     - ✅
     - ✅
   * - **EMAC0 (eth0)**
     - ✅
     - ✅
     - ✅
     - ✅
   * - **EMAC1**<sup>1</sup>
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
   * - **eMMC** _dev/mmcblk0_
     - ➖
     - __N/A__
     - __N/A__
     - __N/A__
   * - **eMMC Boot**<sup>2</sup>
     - ➖
     - __N/A__
     - __N/A__
     - __N/A__
   * - **SD** _dev/mmcblk0_
     - ✅
     - __N/A__
     - ✅
     - ✅
   * - **SD Boot**
     - ✅
     - __N/A__
     - ✅
     - ✅
   * - **OSPI**<sup>3</sup>
     - __N/A__
     - ✅
     - __N/A__
     - __N/A__
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


<sup>✅: PASS</sup>

<sup>❗: PASS with warning(s), see note</sup>

<sup>❌: FAIL, see note</sup>

<sup>**N/A**: Not Applicable</sup>

<sup>➖:  Not Tested</sup>

---------------------------------------------------------

<sup>1</sup>Currently, the driver can enumerate the IP and identify what is present, but the PHY is not configured/reset yet and so ethernet communications do not work.

<sup>2</sup>Currently, booting with the kernel and RFS on SD/eMMC is the only supported configuration, primarily due to hardware limitations on the SOM-CRR-EZKIT (so U-Boot must still be stored on the SPI flash)

<sup>3</sup>OSPI is not currently working for MX66 chip under 5.x and 6.x kernel versions. OSPI (1S-8S-8S mode) does work for SC594 & IS25LX256