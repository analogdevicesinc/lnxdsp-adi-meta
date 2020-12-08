#!/bin/sh

if [ ! -f /lib/firmware/mcapi_send_recv_Core1_sc594.ldr ]; then
	ln -s /remoteproc/mcapi_send_recv_Core1_sc594.ldr /lib/firmware/mcapi_send_recv_Core1_sc594.ldr
fi

if [ ! -f /lib/firmware/mcapi_send_recv_Core2_sc594.ldr ]; then
	ln -s /remoteproc/mcapi_send_recv_Core2_sc594.ldr /lib/firmware/mcapi_send_recv_Core2_sc594.ldr
fi

echo mcapi_send_recv_Core1_sc594.ldr > /sys/class/remoteproc/remoteproc0/firmware
echo start > /sys/class/remoteproc/remoteproc0/state

sleep 1

echo mcapi_send_recv_Core2_sc594.ldr > /sys/class/remoteproc/remoteproc1/firmware
echo start > /sys/class/remoteproc/remoteproc1/state

sleep 1

arm_sharc_msg_demo -r 5
