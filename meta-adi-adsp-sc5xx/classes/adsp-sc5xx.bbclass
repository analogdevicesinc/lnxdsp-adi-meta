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

IMAGE_FSTYPES:append = " tar.xz ubi ext4"

# sc573-ezlite has limited SPI flash; the minimal image is too large for a UBI
# volume, so UBI is only produced for the tiny image on that machine.
IMAGE_FSTYPES:remove:adsp-sc573-ezlite = "ubi"

# Keep the freedesktop MIME database out of the rootfs. Nothing here consumes
# MIME types, but wrynose pulls the database in along two independent weak
# edges, so both have to be named:
#
#   systemd       RRECOMMENDS systemd-mime       (systemd_259.5.bb, new in wrynose)
#   glib-2.0      RRECOMMENDS shared-mime-info   (glib.inc)
#
# and mime.bbclass then injects a *hard* RDEPENDS on shared-mime-info-data into
# any package shipping ${datadir}/mime/packages/*.xml, which systemd-mime does.
# Excluding only one edge leaves the other pulling the data back in; on
# adsp-sc5xx-minimal the systemd edge alone was worth 12 LEBs, both together 66.
#
# glib itself stays, and should: libgpiod RDEPENDS on glib-2.0-utils, so glib is
# a genuine runtime dependency of this image rather than MIME collateral.
BAD_RECOMMENDATIONS += "systemd-mime shared-mime-info"

UBI_VOLNAME = "rootfs"
UBINIZE_ARGS = "-m 1 -p 65536 -s 1"

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

    # Copy fitImage
    if [ -f ${DEPLOY_DIR_IMAGE}/fitImage ]; then
        cp ${DEPLOY_DIR_IMAGE}/fitImage ${PROG_DIR}/
    fi

    # Copy rootfs.ubi
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
