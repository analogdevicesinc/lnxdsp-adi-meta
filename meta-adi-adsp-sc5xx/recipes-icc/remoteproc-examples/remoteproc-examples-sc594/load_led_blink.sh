#!/bin/sh

mkdir -p /lib/firmware

if [ ! -f /lib/firmware/LED_Blink_SC594_SHARC_Core1.ldr ]; then
	ln -s /remoteproc/LED_Blink_SC594_SHARC_Core1.ldr /lib/firmware/LED_Blink_SC594_SHARC_Core1.ldr
fi

if [ ! -f /lib/firmware/LED_Blink_SC594_SHARC_Core2.ldr ]; then
	ln -s /remoteproc/LED_Blink_SC594_SHARC_Core2.ldr /lib/firmware/LED_Blink_SC594_SHARC_Core2.ldr
fi


echo LED_Blink_SC594_SHARC_Core1.ldr > /sys/class/remoteproc/remoteproc0/firmware
echo start > /sys/class/remoteproc/remoteproc0/state

sleep 1

echo ""
echo "LED 2/3/4 should now be blinking on the SOM module (controlled by core 0)"
echo ""

sleep 1

echo LED_Blink_SC594_SHARC_Core2.ldr > /sys/class/remoteproc/remoteproc1/firmware
echo start > /sys/class/remoteproc/remoteproc1/state

sleep 1

echo ""
echo "LED 7/8/9 should now be blinking on the SOM carrier board (controlled by core 1)"
echo ""

echo ""
echo "Done!"
