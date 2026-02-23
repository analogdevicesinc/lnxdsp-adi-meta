============================
Setting Default Boot Method
============================

In order to change the default boot method (i.e., boot used by autoboot or executed after issuing ``boot`` within U-Boot shell), run the following within U-Boot:

.. shell::

   => setenv bootcmd <boot method>
   => saveenv

Boot method can be set to the following:

* ``spiboot``
* ``ramboot``
* ``nfsboot``
* ``mmcboot``
* ``usbboot``

Important Notes
===============

* ``sfdev`` must be set when trying to boot with spiboot. Default QSPI usually requires sfdev to be ``2:1``
* ``usbboot`` must be defined as per the USB boot guide prior to attempting boot with this method
