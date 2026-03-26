Debugging without ICE Hardware
===============================

Introduction
------------

If a dedicated ICE debugger is not available, it is still possible to load firmware into memory using the USB Debug Agent port available on some carrier boards.

.. note::

   Although possible, it is always recommended to use a dedicated ICE-1000/2000 debugger for faster and more flexible debugging.

Limitations
-----------

The following regularly used OpenOCD feature set may not work as intended or have undefined behaviours when using the USB Debug Agent:

* Flashing QSPI
* Hard reset
* Loading successive firmware files

Setup Instructions
------------------

Step 1: Configure Board Boot Mode
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Switch the board to boot mode 0 and press RESET.

Step 2: Configure JTAG Interface Switches
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Set all JTAG interface switches to the required configuration:

* **SOMCRR-EZKIT**: SW1 (all ON) - `See EV-SOMCRR-EZKIT Manual, Table 3-5 <https://www.analog.com/media/en/technical-documentation/user-guides/ev-somcrr-ezkit-manual.pdf>`_
* **SOMCRR-EZLITE**: S4 (1-6 ON, 7-8 OFF) - `See EV-SOMCRR-EZLITE Manual, Table 3-3 <https://www.analog.com/media/en/technical-documentation/user-guides/ev-somcrr-ezlite_manual.pdf>`_

.. warning::

   This will disable the JTAG interface used by the ICE debugger.

Step 3: Connect USB Debug Agent
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Connect the USB Debug Agent on the carrier board to the host PC via USB cable.

Step 4: Start OpenOCD
~~~~~~~~~~~~~~~~~~~~~~

Start OpenOCD with the ``adi-debugagent`` interface:

.. code-block:: shell

   $sdk_usr/bin/openocd -f $sdk_usr/share/openocd/scripts/interface/adi-dbgagent.cfg -f $sdk_usr/share/openocd/scripts/board/ev-sc598-som.cfg -c init -c reset -c halt

Where ``sdk_usr`` is the SDK installation's ``usr`` directory, for example:

.. code-block:: text

   /opt/adi-distro-glibc/5.0.1/sysroots/x86_64-adi_glibc_sdk-linux/usr/

.. note::

   Replace the target/board configuration file as required by your hardware.

Step 5: Connect GDB to OpenOCD
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In a separate terminal, connect GDB to OpenOCD:

.. code-block:: shell

   $sdk_usr/bin/aarch64-adi_glibc-linux/aarch64-adi_glibc-linux-gdb -ex "tar ext :3333"

Loading Firmware
----------------

Once connected, load the firmware using GDB:

.. code-block:: shell

   (gdb) load u-boot-proper-sc598-som-ezkit.elf
   (gdb) c

The firmware will be loaded into memory and execution will begin.

Soft Reset via RCU
------------------

To perform a soft reset through the Reset Control Unit (RCU):

.. code-block:: shell

   (gdb) set *0x3108C000=0x1

This command triggers a system reset without requiring physical hardware reset.

References
----------

* `EV-SOMCRR-EZKIT Manual <https://www.analog.com/media/en/technical-documentation/user-guides/ev-somcrr-ezkit-manual.pdf>`_ (Table 3-5: JTAG Switch Configuration)
* `EV-SOMCRR-EZLITE Manual <https://www.analog.com/media/en/technical-documentation/user-guides/ev-somcrr-ezlite_manual.pdf>`_ (Table 3-3: JTAG Switch Configuration)
