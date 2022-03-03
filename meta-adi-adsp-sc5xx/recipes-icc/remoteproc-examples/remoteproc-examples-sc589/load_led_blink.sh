#!/bin/sh

echo stop > /sys/class/remoteproc/remoteproc0/state
echo stop > /sys/class/remoteproc/remoteproc1/state

#Enable LEDs on soft switch config
cd /sys/class/gpio
echo 482 > export && echo out > gpio482/direction && echo 0 > gpio482/value
echo 483 > export && echo out > gpio483/direction && echo 0 > gpio483/value

cd /remoteproc

mkdir -p /lib/firmware

if [ ! -f /lib/firmware/Toggle_LED_GPIO_SC589_SHARC_Core1.ldr ]; then
	ln -s /remoteproc/Toggle_LED_GPIO_SC589_SHARC_Core1.ldr /lib/firmware/Toggle_LED_GPIO_SC589_SHARC_Core1.ldr
fi

if [ ! -f /lib/firmware/Toggle_LED_GPIO_SC589_SHARC_Core2.ldr ]; then
	ln -s /remoteproc/Toggle_LED_GPIO_SC589_SHARC_Core2.ldr /lib/firmware/Toggle_LED_GPIO_SC589_SHARC_Core2.ldr
fi

echo Toggle_LED_GPIO_SC589_SHARC_Core1.ldr > /sys/class/remoteproc/remoteproc0/firmware
echo start > /sys/class/remoteproc/remoteproc0/state

sleep 1

echo ""
echo "LED 11 should now be blinking (controlled by core 0)"
echo ""

sleep 5

echo Toggle_LED_GPIO_SC589_SHARC_Core2.ldr > /sys/class/remoteproc/remoteproc1/firmware
echo start > /sys/class/remoteproc/remoteproc1/state

sleep 1

echo ""
echo "LED 10 should now be blinking (controlled by core 1)"
echo ""

echo ""
echo "Done!"
