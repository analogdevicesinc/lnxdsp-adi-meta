#!/bin/sh

echo stop > /sys/class/remoteproc/remoteproc0/state
echo stop > /sys/class/remoteproc/remoteproc1/state

mkdir -p /lib/firmware

if [ ! -f /lib/firmware/mcapi_send_recv_sc589_Core1.ldr ]; then
	ln -s /remoteproc/mcapi_send_recv_sc589_Core1.ldr /lib/firmware/mcapi_send_recv_sc589_Core1.ldr
fi

if [ ! -f /lib/firmware/mcapi_send_recv_sc589_Core2.ldr ]; then
	ln -s /remoteproc/mcapi_send_recv_sc589_Core2.ldr /lib/firmware/mcapi_send_recv_sc589_Core2.ldr
fi


echo mcapi_send_recv_sc589_Core1.ldr > /sys/class/remoteproc/remoteproc0/firmware
echo mcapi_send_recv_sc589_Core2.ldr > /sys/class/remoteproc/remoteproc1/firmware
echo start > /sys/class/remoteproc/remoteproc0/state
echo start > /sys/class/remoteproc/remoteproc1/state

sleep 1

arm_sharc_msg_demo -r 5
