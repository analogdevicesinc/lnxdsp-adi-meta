inherit core-image extrausers
SUMMARY = "Minimal ramdisk image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-base \
    busybox-watchdog-init \
"

#Add this back in to IMAGE_INSTALL if you want to perform a switch_root
# initramfs-init

DISTRO_FEATURES = " ram"
IMAGE_FSTYPES = " cpio.xz"

DEPENDS += "u-boot-tools-native"
do_adi_ramdisk[depends] = "u-boot-adi:do_compile"
do_adi_ramdisk(){
    #Format the cpio image for u-boot
    mkimage -n 'Analog Devices Ram Disk Image'  -A ${UBOOT_ARCH} -O linux -T ramdisk -C gzip -d ${WORKDIR}/deploy-${PN}-image-complete/${PN}-${MACHINE}.cpio.xz ${DEPLOY_DIR_IMAGE}/${PN}-${MACHINE}.cpio.xz.u-boot
}

addtask adi_ramdisk after do_image_cpio before do_image_complete

COMPATIBLE_MACHINE = "(adsp-sc573-ezkit|adsp-sc584-ezkit|adsp-sc589-ezkit|adsp-sc589-mini|adsp-sc594-som-ezkit|adsp-sc598-som-ezkit)"

EXTRA_USERS_PARAMS = "usermod -P adi root;"
