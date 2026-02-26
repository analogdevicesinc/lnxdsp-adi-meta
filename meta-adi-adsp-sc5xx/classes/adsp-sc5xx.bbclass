inherit core-image extrausers adsp-sc5xx-compatible adsp-fit-generation

SUMMARY = "Minimal image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

INITRD_NAME = "adsp-sc5xx-ramdisk-${MACHINE}.cpio.gz"

SHARC_ALSA_BINARIES = "${@bb.utils.contains_any('DISTRO_FEATURES', 'adi_sharc_alsa_audio', 'sharc-audio', '', d)}"
HYBRID_BINARIES = "${@bb.utils.contains_any('DISTRO_FEATURES', 'adi_hybrid_audio', 'hybrid-audio', '', d)}"
LINUX_ONLY_BINARIES = "${@bb.utils.contains_any('DISTRO_FEATURES', 'linux_only_audio', 'rpmsg-echo-example', '', d)}"

ICC = " \
	rpmsg-utils \
	${SHARC_ALSA_BINARIES} \
	${HYBRID_BINARIES} \
	${LINUX_ONLY_BINARIES} \
"

CRYPTO = " \
    openssl \
    openssl-bin \
    cryptodev-linux \
    cryptodev-module \
    crypto-tests \
    crc-tests \
"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-base \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    alsa-utils \
    openssh \
    openssl \
    iproute2 \
    iproute2-tc \
    ncurses \
    busybox-watchdog-init \
    util-linux \
    rng-tools \
    spidev-test \
    spitools \
    mtd-utils \
    mtd-utils-ubifs \
    e2fsprogs \
    ${ICC} \
    ${CRYPTO} \
    libgpiod libgpiod-tools \
"

# printf "%q" $(mkpasswd -m sha256crypt adi)
PASSWD_ROOT = "\$5\$j9T8zDE13LXUGyc6\$utDvGwFWR.kt/AKwwbHnXC14HJBqbcWwvLoDDLMQrc8"
EXTRA_USERS_PARAMS = "usermod -p '${PASSWD_ROOT}' root;"

TOOLCHAIN_HOST_TASK:append = " nativesdk-openocd-adi"
TOOLCHAIN_HOST_TASK:append = " nativesdk-ldr-adi"

IMAGE_FSTYPES:append = " tar.xz ubifs "

#For some reason on poky-tiny, INIT_MANAGER="systemd" does not appear to work
#For now, let's relink directly to systemd
fakeroot do_set_init(){
    if [ "${DISTRO}" = "adi-distro-musl" ]; then
        rm -rf ${IMAGE_ROOTFS}/sbin/init
        ln -s /lib/systemd/systemd ${IMAGE_ROOTFS}/sbin/init
    fi
}

addtask do_set_init after do_rootfs before do_image

fakeroot do_install_init_script(){
    # Create firmware directory in rootfs and install init script
    install -d ${IMAGE_ROOTFS}/usr/firmware
    # Copy init script directly from the classes files directory
    install -m 755 ${THISDIR}/files/init ${IMAGE_ROOTFS}/usr/firmware/init
}

addtask install_init_script after do_set_init before do_image

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

    # Copy fitImage
    if [ -f ${DEPLOY_DIR_IMAGE}/fitImage ]; then
        cp ${DEPLOY_DIR_IMAGE}/fitImage ${PROG_DIR}/
    fi

    # Copy rootfs.ubifs, rename to 'rootfs.ubifs'
    if [ -f ${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.rootfs.ubifs ]; then
        cp ${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.rootfs.ubifs ${PROG_DIR}/rootfs.ubifs
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
