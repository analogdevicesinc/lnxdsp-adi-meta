#!/bin/sh

PATH=/sbin:/bin:/usr/sbin:/usr/bin

do_mount_fs() {
	grep -q "$1" /proc/filesystems || return
	test -d "$2" || mkdir -p "$2"
	mount -t "$1" "$1" "$2"
}

do_mknod() {
	test -e "$1" || mknod "$1" "$2" "$3" "$4"
}

mkdir /proc
mount -t proc proc /proc
mkdir /tmp

do_mount_fs sysfs /sys
do_mount_fs debugfs /sys/kernel/debug
do_mount_fs devtmpfs /dev
do_mount_fs devpts /dev/pts
do_mount_fs tmpfs /dev/shm

RFS_DEVICE=$(cat /proc/cmdline | sed -e 's/^.*root=//' -e 's/ .*$//')

mkdir -p /rootmount

mount -t ext2 $RFS_DEVICE /rootmount

echo "Booting RFS from ${RFS_DEVICE}"

exec switch_root /rootmount /sbin/init
