#!/bin/sh

PATH=/sbin:/bin:/usr/sbin:/usr/bin

do_splash(){
printf " \

         Analog Initial Ram Filesystem
                www.analog.com
              www.yoctoproject.org

Analog [Initramfs]: Preparing Operating System....
Analog [Initramfs]: Mounting Root File System...
" > /dev/kmsg
}

do_mount_fs() {
	grep -q "$1" /proc/filesystems || return
	test -d "$2" || mkdir -p "$2"
	mount -t "$1" "$1" "$2"
}

mkdir -p /proc
mkdir -p /rootmount
mount -t proc proc /proc

do_mount_fs sysfs /sys
do_mount_fs debugfs /sys/kernel/debug
do_mount_fs devtmpfs /dev
do_mount_fs devpts /dev/pts
do_mount_fs tmpfs /dev/shm

do_splash

if [ "$(grep nfsroot= /proc/cmdline)" ]; then
	NFS_ROOT=$(cat /proc/cmdline | sed -e 's/^.*nfsroot=//' -e 's/ .*$//')
	NFS_SERVER=$(printf ${NFS_ROOT} | sed -e 's/,.*//')
	NFS_OPTS=$(printf ${NFS_ROOT} | sed -e 's/,/REP/' -e 's/.*REP//')
	echo "Analog [Initramfs]: Switching RFS to NFS mount (${NFS_OPTS},${NFS_SERVER})..." > /dev/kmsg
	mount -t nfs -o nolock,$NFS_OPTS $NFS_SERVER /rootmount
	exec switch_root /rootmount /sbin/init > /dev/kmsg
elif [ "$(grep root= /proc/cmdline)" ]; then
	RFS_DEVICE=$(cat /proc/cmdline | sed -e 's/^.*root=//' -e 's/ .*$//')
	RFS_TYPE=$(cat /proc/cmdline | sed -e 's/^.*rootfstype=//' -e 's/ .*$//')
	echo "Analog [Initramfs]: Switching RFS to ${RFS_TYPE},${RFS_DEVICE}..." > /dev/kmsg
	mount -t $RFS_TYPE $RFS_DEVICE /rootmount
	exec switch_root /rootmount /sbin/init > /dev/kmsg
else
	echo "Analog [Initramfs]: No root device found, dropping to getty" > /dev/kmsg

	while [ 1 ]; do
		/sbin/getty 115200 /dev/ttySC0
	done
fi