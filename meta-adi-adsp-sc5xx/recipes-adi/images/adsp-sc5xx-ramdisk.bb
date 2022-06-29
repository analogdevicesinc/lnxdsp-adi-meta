inherit core-image extrausers
SUMMARY = "Minimal ramdisk image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-base \
    busybox-watchdog-init \
"

#For transferring data to flash eMMC via ramdisk
IMAGE_INSTALL += " \
    openssh \
    e2fsprogs-resize2fs \
    gzip \
    adi-flash-emmc \
    mtd-utils \
    mtd-utils-ubifs \
"

#Add this back in to IMAGE_INSTALL if you want to perform a switch_root
# initramfs-init

DISTRO_FEATURES = " ram"
IMAGE_FSTYPES = " cpio.xz"

DEPENDS += "u-boot-tools-native"

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
}

addtask rootfs_cleanup after do_rootfs before do_image

do_adi_ramdisk[depends] = "virtual/bootloader:do_compile"
do_adi_ramdisk(){
    #Format the cpio image for u-boot
    mkimage -n 'Analog Devices Ram Disk Image'  -A ${UBOOT_ARCH} -O linux -T ramdisk -C gzip -d ${WORKDIR}/deploy-${PN}-image-complete/${PN}-${MACHINE}.cpio.xz ${DEPLOY_DIR_IMAGE}/${PN}-${MACHINE}.cpio.xz.u-boot
}

addtask adi_ramdisk after do_image_cpio before do_image_complete

COMPATIBLE_MACHINE = "(adsp-sc573-ezkit|adsp-sc584-ezkit|adsp-sc589-ezkit|adsp-sc589-mini|adsp-sc594-som-ezkit|adsp-sc598-som-ezkit)"

EXTRA_USERS_PARAMS = "usermod -P adi root;"
