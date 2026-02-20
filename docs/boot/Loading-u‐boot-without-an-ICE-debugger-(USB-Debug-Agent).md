If a dedicated ICE debugger is not available, it is still possible to load firmware into memory.

## Unsupported feature set

NOTE: Although possible, it is always recommended to use a dedicated ICE-1000/2000 debugger for faster and more flexible debugging. 

The following regularly used openOCD feature set may not work as intended/have undefined behaviours:
- flashing QSPI
- hard reset
- Loading successive firmware files

## Getting started

1) Switch board to bootmode 0, press RESET
2) Set all JTAG interface switches to the required configuration
- SW1 (all ON) on the SOMCRR-EZKIT [[1]](https://www.analog.com/media/en/technical-documentation/user-guides/ev-somcrr-ezkit-manual.pdf) 
- S4 (1-6 ON, 7-8 OFF) for SOMCRR-EZLITE [[2]](https://www.analog.com/media/en/technical-documentation/user-guides/ev-somcrr-ezlite_manual.pdf) 

**NOTE: This will disable the JTAG interface used by the ICE debugger.**

3) Connect the USB Debug Agent on the carrier board with the host.
4) Start openOCD with the `adi-debugagent` interface
```
$sdk_usr/bin/openocd -f $sdk_usr/share/openocd/scripts/interface/adi-dbgagent.cfg  -f $sdk_usr/share/openocd/scripts/board/ev-sc598-som.cfg -c init -c reset -c halt
```
where `sdk_usr` is the SDK installation's `usr` directory and may correspond to something like the following
```
/opt/adi-distro-glibc/3.1.0/sysroots/x86_64-adi_glibc_sdk-linux/usr/
```

NOTE: Replace the target/board as required by the hardware

5) Connect gdb to openOCD
```
$sdk_usr/bin/aarch64-adi_glibc-linux/aarch64-adi_glibc-linux-gdb -ex "tar ext :3333"
```

## Loading Firmware
Connect GDB to openOCD
```
 (gdb) load u-boot-proper-sc598-som-ezkit.elf
```
```
 (gdb) c
```

## Soft reset via RCU
```
 (gdb) set *0x3108C000=0x1
```


## Sources:
[1] https://www.analog.com/media/en/technical-documentation/user-guides/ev-somcrr-ezkit-manual.pdf (*See table 3-5*)

[2] https://www.analog.com/media/en/technical-documentation/user-guides/ev-somcrr-ezlite_manual.pdf (*See table 3-3*)