inherit adsp-sc5xx-minimal

#We do not need these files in the rootfs -- remove them to reduce the minimal rootfs size
rootfs_cleanup(){
	rm -rf ${IMAGE_ROOTFS}/boot

	rm -rf ${IMAGE_ROOTFS}/etc/udev/hwdb.bin
	rm -rf ${IMAGE_ROOTFS}/etc/udev/hwdb.d

	rm -rf ${IMAGE_ROOTFS}/usr/lib/locale
	rm -rf ${IMAGE_ROOTFS}/usr/lib/opkg

	rm -rf ${IMAGE_ROOTFS}/usr/lib/libX11.so.6
	rm -rf ${IMAGE_ROOTFS}/usr/lib/libX11.so.6.3.0
}

ROOTFS_POSTPROCESS_COMMAND += "rootfs_cleanup"
