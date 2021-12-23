#!/bin/sh

mkdir -p /lib/firmware

if [ ! -f /lib/firmware/mcapi_send_recv_Core1_sc589.ldr ]; then
	ln -s /remoteproc/mcapi_send_recv_Core1_sc589.ldr /lib/firmware/mcapi_send_recv_Core1_sc589.ldr
fi

echo mcapi_send_recv_Core1_sc589.ldr > /sys/class/remoteproc/remoteproc0/firmware
echo start > /sys/class/remoteproc/remoteproc0/state

sleep 1

arm_sharc_msg_demo -r 5 -i 1
