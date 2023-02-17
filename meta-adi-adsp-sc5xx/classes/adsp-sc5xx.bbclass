inherit core-image extrausers adsp-sc5xx-compatible adsp-fit-generation

SUMMARY = "Minimal image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

INITRD_NAME = "adsp-sc5xx-ramdisk-${MACHINE}.cpio.gz"

ICC = " \
	rpmsg-utils \
"

CRYPTO = " \
    openssl \
    openssl-bin \
    cryptodev-linux \
    cryptodev-module \
    crypto-tests \
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
    ${ICC} \
    ${CRYPTO} \
    libgpiod libgpiod-tools \
"

EXTRA_USERS_PARAMS = " \
	usermod -P adi root; \
"

TOOLCHAIN_HOST_TASK_append += " nativesdk-openocd-adi"
