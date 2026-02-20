===================
Test Report (3.1.0)
===================



.. list-table::
   :header-rows: 1

   * - :memo:
     - This test report applies to versions 3.1.x. Any regressions specific to a .x release will be explicitly noted



.. list-table::
   :header-rows: 1

   * - Testing Component
     - SC598-SOM-EZKIT
     - SC594-SOM-EZKIT
     - SC589-EZKIT
     - SC589-MINI
     - SC584-EZKIT
     - SC573-EZKIT
   * - **SRU** _DAI routing configuration_
     - 
     - 
     - 
     - 
     - 
     - 
   * - **SPORT** _sending I2S data to CODECs_
     - 
     - 
     - 
     - 
     - 
     - 
   * - **ADAU1761** _Audio In Driver_
     - 
     - 
     - 
     - 
     - 
     - 
   * - **ADAU1962** _Audio Out Driver_
     - ✅
     - ✅
     - ✅
     - ✅
     - 
     - ✅
   * - **ADAU1979** _Audio In Driver_
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
     - __N/A__
     - __N/A__
     - __N/A__
     - __N/A__
   * - **DMA Peripheral** _Implicitly tested as it's used by SPI, OSPI, and UART drivers_
     - ✅
     - ✅
     - 
     - ✅
     - 
     - 
   * - **MDMA** _Implicitly tested as it's used only by remoteproc to load data to SHARC shared memory_
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
   * - **EMAC1**<sup>1</sup>
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
   * - **eMMC** _dev/mmcblk0_
     - ➖
     - __N/A__
     - __N/A__
     - __N/A__
     - __N/A__
     - __N/A__
   * - **eMMC Boot**<sup>3</sup>
     - ➖
     - __N/A__
     - __N/A__
     - __N/A__
     - __N/A__
     - __N/A__
   * - **SD** _dev/mmcblk0_
     - ✅
     - __N/A__
     - ✅
     - ✅
     - __N/A__
     - ✅
   * - **SD Boot**
     - ✅
     - __N/A__
     - ✅
     - ✅
     - __N/A__
     - ✅
   * - **OSPI**<sup>4</sup>
     - __N/A__
     - ✅
     - __N/A__
     - __N/A__
     - __N/A__
     - __N/A__
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


<sup>✅: PASS</sup>

<sup>❗: PASS with warning(s), see note</sup>

<sup>**N/A**: Not Applicable</sup>

<sup>➖:  Not Tested</sup>

---------------------------------------------------------

<sup>1</sup>Currently, the driver can enumerate the IP and identify what is present, but the PHY is not configured/reset yet and so ethernet communications do not work.

<sup>2</sup>There is a known issue on the SC589-MINI's watchdog which prevents the RCU driver/system from resetting properly

<sup>3</sup>Currently, booting with the kernel and RFS on SD/eMMC is the only supported configuration, primarily due to hardware limitations on the SOM-CRR-EZKIT (so U-Boot must still be stored on the SPI flash)

<sup>4</sup>OSPI is not currently working for MX66 chip under 5.15 kernel. OSPI (1S-8S-8S mode) does work for SC594 & IS25LX256