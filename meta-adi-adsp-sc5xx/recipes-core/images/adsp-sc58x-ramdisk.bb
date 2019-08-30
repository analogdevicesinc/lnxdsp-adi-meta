inherit core-image extrausers
SUMMARY = "Minimal ramdisk image for Analog Devices ADSP-SC58x boards"
LICENSE = "MIT"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-base \
    busybox-watchdog-init \
"

DISTRO_FEATURES = " ram"
IMAGE_FSTYPES = " cpio.xz"

do_adi_ramdisk(){
    #Find mkimage utility inside u-boot source directory
    MKIMAGE=${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}/u-boot-adi/*/build/tools/mkimage

    #Format the cpio image for u-boot
    ${MKIMAGE} -n 'Analog Devices Ram Disk Image'  -A arm -O linux -T ramdisk -C gzip -d ${DEPLOY_DIR_IMAGE}/adsp-sc58x-ramdisk-${MACHINE}.cpio.xz ${DEPLOY_DIR_IMAGE}/adsp-sc58x-ramdisk-${MACHINE}.cpio.xz.u-boot
}

addtask adi_ramdisk after do_deploy

COMPATIBLE_MACHINE = "(adsp-sc584-ezkit|adsp-sc589-ezkit|adsp-sc589-mini)"

EXTRA_USERS_PARAMS = "usermod -P adi root;"