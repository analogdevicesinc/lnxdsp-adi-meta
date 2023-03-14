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

# printf "%q" $(mkpasswd -m sha256crypt adi)
PASSWD_ROOT = "\$5\$j9T8zDE13LXUGyc6\$utDvGwFWR.kt/AKwwbHnXC14HJBqbcWwvLoDDLMQrc8"
EXTRA_USERS_PARAMS = "usermod -p '${PASSWD_ROOT}' root;"

TOOLCHAIN_HOST_TASK:append += " nativesdk-openocd-adi"
