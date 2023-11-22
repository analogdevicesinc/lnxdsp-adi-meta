#!/bin/sh

#If not already loaded at boot, then load the firmware:

#echo "echo_core1.ldr" > /sys/class/remoteproc/remoteproc0/firmware
#echo "start" > /sys/class/remoteproc/remoteproc0/state

#sleep 2

#echo "echo_core2.ldr" > /sys/class/remoteproc/remoteproc1/firmware
#echo "start" > /sys/class/remoteproc/remoteproc1/state

#sleep 2

rpmsg-bind-chardev -p virtio0.sharc-echo-.-1. -n 1 -e 151 -s 50
echo hello | rpmsg-xmit -n 5 /dev/rpmsg0

rpmsg-bind-chardev -p virtio0.sharc-echo-cap.-1. -n 1 -e 161 -s 61
echo hello | rpmsg-xmit -n 5 /dev/rpmsg1
