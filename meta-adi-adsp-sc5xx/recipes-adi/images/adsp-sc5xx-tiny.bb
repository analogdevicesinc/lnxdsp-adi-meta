inherit core-image extrausers adsp-sc5xx-compatible

SUMMARY = "Tiny image for Analog Devices ADSP-SC5xx boards with 16MB SPI"
LICENSE = "MIT"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-base \
    busybox-watchdog-init \
"

EXTRA_USERS_PARAMS = " \
	usermod -P adi root; \
"

IMAGE_FSTYPES = " jffs2"

#We do not need these files in the rootfs -- remove them to reduce the minimal rootfs size
fakeroot do_rootfs_cleanup(){
	rm -rf ${IMAGE_ROOTFS}/boot
	rm -rf ${IMAGE_ROOTFS}/lib/udev/hwdb.bin
	rm -rf ${IMAGE_ROOTFS}/lib/udev/hwdb.d
	rm -rf ${IMAGE_ROOTFS}/usr/lib/opkg
	rm -rf ${IMAGE_ROOTFS}/usr/lib/locale
	rm -rf ${IMAGE_ROOTFS}/etc/X11
	rm -rf ${IMAGE_ROOTFS}/usr/share/consolefonts
	rm -rf ${IMAGE_ROOTFS}/usr/share/alsa
	rm -rf ${IMAGE_ROOTFS}/usr/share/keymaps
	rm -rf ${IMAGE_ROOTFS}/usr/lib/libX11.so.6.3.0
	rm -rf ${IMAGE_ROOTFS}/usr/lib/libX11.so.6
	rm -rf ${IMAGE_ROOTFS}/usr/lib/libasound.so.2.0.0
	rm -rf ${IMAGE_ROOTFS}/usr/lib/libasound.so.2
	rm -rf ${IMAGE_ROOTFS}/usr/lib/libxcb.so.1.1.0
	rm -rf ${IMAGE_ROOTFS}/usr/lib/libxcb.so.1
	rm -rf ${IMAGE_ROOTFS}/sbin/fsck.ext2
	rm -rf ${IMAGE_ROOTFS}/sbin/fsck.ext3
	rm -rf ${IMAGE_ROOTFS}/etc/ssh/moduli
	rm -rf ${IMAGE_ROOTFS}/usr/sbin/alsactl
	rm -rf ${IMAGE_ROOTFS}/usr/sbin/useradd
	rm -rf ${IMAGE_ROOTFS}/usr/sbin/userdel
	rm -rf ${IMAGE_ROOTFS}/usr/sbin/usermod
}

addtask rootfs_cleanup after do_rootfs before do_image
