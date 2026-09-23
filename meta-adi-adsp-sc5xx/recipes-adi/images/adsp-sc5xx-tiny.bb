inherit core-image extrausers adsp-sc5xx-compatible adsp-fit-generation

SUMMARY = "Tiny image for Analog Devices ADSP-SC5xx boards with 16MB SPI"
LICENSE = "MIT"

IMAGE_INSTALL = " \
    packagegroup-base \
    busybox-watchdog-init \
    mtd-utils \
    mtd-utils-ubifs \
    rpmsg-echo-example \
"

# printf "%q" $(mkpasswd -m sha256crypt adi)
PASSWD_ROOT = "\$5\$j9T8zDE13LXUGyc6\$utDvGwFWR.kt/AKwwbHnXC14HJBqbcWwvLoDDLMQrc8"
EXTRA_USERS_PARAMS = "usermod -p '${PASSWD_ROOT}' root;"

IMAGE_FSTYPES = " tar.xz ubi ext4"

UBI_VOLNAME = "rootfs"
UBINIZE_ARGS = "-m 1 -p 65536 -s 1"
MKUBIFS_ARGS:append = " -x zlib"

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

ADSP_SC5XX_INIT_SCRIPT := "${THISDIR}/files/init"

fakeroot do_install_init_script(){
    # Create firmware directory in rootfs and install init script
    install -d ${IMAGE_ROOTFS}/usr/firmware
    install -m 755 ${ADSP_SC5XX_INIT_SCRIPT} ${IMAGE_ROOTFS}/usr/firmware/init
}

addtask install_init_script after do_rootfs before do_image

do_create_programming_images(){
    # Create programming-images directory
    PROG_DIR="${DEPLOY_DIR_IMAGE}/programming-images/${IMAGE_BASENAME}"
    install -d ${PROG_DIR}

    # Copy U-boot ldr images
    if [ -f ${DEPLOY_DIR_IMAGE}/u-boot-spl.ldr ]; then
        cp ${DEPLOY_DIR_IMAGE}/u-boot-spl.ldr ${PROG_DIR}/
    fi
    if [ -f ${DEPLOY_DIR_IMAGE}/u-boot.ldr ]; then
        cp ${DEPLOY_DIR_IMAGE}/u-boot.ldr ${PROG_DIR}/
    fi

    DTB_FILE=$(basename "${KERNEL_DEVICETREE}")
    if [ -f "${DEPLOY_DIR_IMAGE}/$DTB_FILE" ]; then
        cp "${DEPLOY_DIR_IMAGE}/$DTB_FILE" "${PROG_DIR}/dtb"
    fi

    # Copy fitImage
    if [ -f ${DEPLOY_DIR_IMAGE}/fitImage ]; then
        cp ${DEPLOY_DIR_IMAGE}/fitImage ${PROG_DIR}/
    fi

    if [ -f ${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.rootfs.ubi ]; then
        cp ${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.rootfs.ubi ${PROG_DIR}/rootfs.ubi
    fi

    if [ -f ${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.rootfs.ext4 ]; then
        cp ${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.rootfs.ext4 ${PROG_DIR}/rootfs.ext4
    fi

    echo "Programming images created in: ${PROG_DIR}"
    ls -la ${PROG_DIR}
}

addtask create_programming_images after do_image_complete before do_build

do_create_programming_images[depends] += "\
    virtual/bootloader:do_deploy \
    virtual/kernel:do_deploy \
"

do_create_programming_images[vardeps] += "KERNEL_DEVICETREE IMAGE_BASENAME MACHINE"