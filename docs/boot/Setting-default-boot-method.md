# Setting Default Boot Method

In order to change the default boot (i.e, boot used by autoboot or executed after issueing `boot` within u-boot shell), run the following within U-boot:

```
=> setenv bootcmd <boot method>
=> saveenv
```

Boot method can be set to the following:
* `spiboot` 
* `ramboot`
* `nfsboot`
* `mmcboot`
* `usbboot`

Important:
* `sfdev` must be set when trying to boot with spiboot. Default qspi usually requires sfdev to be 2:1
* `usbboot` must be defined as per the usb boot guide prior to attempting boot with this method.