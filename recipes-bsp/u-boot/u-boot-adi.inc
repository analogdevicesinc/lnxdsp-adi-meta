SUMMARY = "U-Boot For ADSP-SC5xx Products"

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=..."

BRANCH ?= "master"
SRC_URI = " \
	git://... \
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
