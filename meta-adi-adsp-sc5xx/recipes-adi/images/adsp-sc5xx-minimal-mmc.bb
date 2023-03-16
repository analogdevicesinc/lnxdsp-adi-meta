inherit adsp-sc5xx-minimal

IMAGE_INSTALL += "kernel kernel-devicetree"

#We do not need these files in the rootfs -- remove them to reduce the minimal rootfs size
fakeroot do_rootfs_cleanup(){

	#We need this for eMMC boot -- do not delete
	#rm -rf ${IMAGE_ROOTFS}/boot

	rm -rf ${IMAGE_ROOTFS}/etc/udev/hwdb.bin
	rm -rf ${IMAGE_ROOTFS}/etc/udev/hwdb.d

	rm -rf ${IMAGE_ROOTFS}/usr/lib/locale
	rm -rf ${IMAGE_ROOTFS}/usr/lib/opkg

	rm -rf ${IMAGE_ROOTFS}/usr/lib/libX11.so.6
	rm -rf ${IMAGE_ROOTFS}/usr/lib/libX11.so.6.3.0
}

addtask rootfs_cleanup after do_rootfs before do_image

IMAGE_FSTYPES:append = " wic.gz ext4 "
