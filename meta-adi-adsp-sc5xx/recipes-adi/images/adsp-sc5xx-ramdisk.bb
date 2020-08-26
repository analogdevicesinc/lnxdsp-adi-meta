inherit core-image
SUMMARY = "Minimal ramdisk image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
	linux-firmware-fastboot \
    fastboot-listener \
"

DISTRO_FEATURES = " ram"
IMAGE_FSTYPES = "cpio"

do_adi_ramdisk[depends] = "u-boot:do_compile"
do_adi_ramdisk(){
    #Find mkimage utility inside u-boot source directory
    MKIMAGE=${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}/u-boot-adi/*/build/tools/mkimage

    #Format the cpio image for u-boot
    ${MKIMAGE} -n 'Analog Devices Ram Disk Image' -A arm -O linux -T ramdisk -C gzip -d ${WORKDIR}/deploy-${PN}-image-complete/${PN}-${MACHINE}.${IMAGE_FSTYPES} ${DEPLOY_DIR_IMAGE}/${PN}-${MACHINE}.${IMAGE_FSTYPES}.u-boot
}

addtask adi_ramdisk after do_image_cpio before do_image_complete

COMPATIBLE_MACHINE = "(adsp-sc573-ezkit|adsp-sc584-ezkit|adsp-sc589-ezkit|adsp-sc589-mini)"