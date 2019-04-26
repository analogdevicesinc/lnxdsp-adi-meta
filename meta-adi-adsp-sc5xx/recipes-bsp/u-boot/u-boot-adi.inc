SUMMARY = "U-Boot For ADSP-SC5xx Products"

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

UBOOT_GIT_URI = "git://git@src.timesys.com/services/analog-devices/analog-devices-yocto-bsp-porting/uboot.git"
UBOOT_GIT_PROTOCOL = "ssh"

SRC_URI += " \
	${UBOOT_GIT_URI};protocol=${UBOOT_GIT_PROTOCOL};branch=${BRANCH} \
"

FILES_${PN} = " \
	u-boot.bin \
"

PV_append = "+git${SRCPV}"

DEPENDS += "dtc-native bc-native lzop-native"

UBOOT_SUFFIX ?= "img"

PROVIDES += "u-boot"
PKG_${PN} = "u-boot"
PKG_${PN}-dev = "u-boot-dev"
PKG_${PN}-dbg = "u-boot-dbg"

S = "${WORKDIR}/git"

do_install () {
	install ${B}/u-boot.bin ${D}/u-boot.bin
}

do_deploy() {
	install ${B}/u-boot.bin ${DEPLOYDIR}/u-boot.bin
}
