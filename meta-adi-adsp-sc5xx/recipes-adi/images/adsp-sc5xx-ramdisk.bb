inherit core-image extrausers adsp-sc5xx-compatible

SUMMARY = "Minimal ramdisk image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

#For transferring data to flash eMMC/SD via ramdisk
MMC_UTILS = " \
    openssh \
    e2fsprogs-resize2fs \
    gzip \
    adi-flash-emmc \
    mtd-utils \
    mtd-utils-ubifs \
"

def emmc_utils(d):
  utils = "${MMC_UTILS}"
  MACHINE = d.getVar('MACHINE')
  if MACHINE == 'adsp-sc594-som-ezkit':
      utils = ""
  elif MACHINE == 'adsp-sc584-ezkit':
      utils = ""
  return utils

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-base \
    busybox-watchdog-init \
    ${@emmc_utils(d)} \
    libgpiod libgpiod-tools \
"

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

do_adi_ramdisk[depends] = "virtual/bootloader:do_compile"
do_adi_ramdisk(){
    #Format the cpio image for u-boot
    mkimage -n 'Analog Devices Ram Disk Image'  -A ${UBOOT_ARCH} -O linux -T ramdisk -C gzip -d ${WORKDIR}/deploy-${PN}-image-complete/${PN}-${MACHINE}.cpio.xz ${DEPLOY_DIR_IMAGE}/${PN}-${MACHINE}.cpio.xz.u-boot
}

addtask adi_ramdisk after do_image_cpio before do_image_complete

EXTRA_USERS_PARAMS = "usermod -P adi root;"
