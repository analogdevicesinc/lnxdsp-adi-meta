inherit adsp-sc5xx-minimal

DEPENDS += "adsp-sc5xx-ramdisk-emmc-tools"

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

        cp ${DEPLOY_DIR_IMAGE}/fitImage ${IMAGE_ROOTFS}/boot
}

addtask rootfs_cleanup after do_assemble_fitimage before do_image_ext4

IMAGE_FSTYPES:append = " wic.gz ext4 "
