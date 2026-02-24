============================================================
Loading U-Boot without an ICE Debugger (USB Debug Agent)
============================================================

If a dedicated ICE debugger is not available, it is still possible to load firmware into memory using the USB Debug Agent port available on some carrier boards.

Unsupported Feature Set
========================

.. note::

   Although possible, it is always recommended to use a dedicated ICE-1000/2000 debugger for faster and more flexible debugging.

The following regularly used OpenOCD feature set may not work as intended or have undefined behaviours:

* Flashing QSPI
* Hard reset
* Loading successive firmware files

Getting Started
===============

1. Switch board to bootmode 0, press RESET

2. Set all JTAG interface switches to the required configuration:

   * SW1 (all ON) on the SOMCRR-EZKIT - `See EV-SOMCRR-EZKIT Manual, Table 3-5 <https://www.analog.com/media/en/technical-documentation/user-guides/ev-somcrr-ezkit-manual.pdf>`_
   * S4 (1-6 ON, 7-8 OFF) for SOMCRR-EZLITE - `See EV-SOMCRR-EZLITE Manual, Table 3-3 <https://www.analog.com/media/en/technical-documentation/user-guides/ev-somcrr-ezlite_manual.pdf>`_

.. warning::

   This will disable the JTAG interface used by the ICE debugger.

3. Connect the USB Debug Agent on the carrier board with the host.

4. Start OpenOCD with the ``adi-debugagent`` interface:

.. shell::

   $sdk_usr/bin/openocd -f $sdk_usr/share/openocd/scripts/interface/adi-dbgagent.cfg  -f $sdk_usr/share/openocd/scripts/board/ev-sc598-som.cfg -c init -c reset -c halt

where ``sdk_usr`` is the SDK installation's ``usr`` directory and may correspond to something like:

.. code-block:: text

   /opt/adi-distro-glibc/5.0.1/sysroots/x86_64-adi_glibc_sdk-linux/usr/

.. note::

   Replace the target/board configuration file as required by your hardware.

5. Connect GDB to OpenOCD:

.. shell::

   $sdk_usr/bin/aarch64-adi_glibc-linux/aarch64-adi_glibc-linux-gdb -ex "tar ext :3333"

Loading Firmware
================

Connect GDB to OpenOCD and load the firmware:

.. shell::

   (gdb) load u-boot-proper-sc598-som-ezkit.elf
   (gdb) c

Soft Reset via RCU
==================

To perform a soft reset through the Reset Control Unit (RCU):

.. shell::

   (gdb) set *0x3108C000=0x1

References
==========

* `EV-SOMCRR-EZKIT Manual <https://www.analog.com/media/en/technical-documentation/user-guides/ev-somcrr-ezkit-manual.pdf>`_ (See Table 3-5)
* `EV-SOMCRR-EZLITE Manual <https://www.analog.com/media/en/technical-documentation/user-guides/ev-somcrr-ezlite_manual.pdf>`_ (See Table 3-3)
