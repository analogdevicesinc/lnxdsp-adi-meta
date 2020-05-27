SUMMARY = "U-Boot For ADSP-SC5xx Products"

inherit analog-devices-baremetal autotools

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot-adi:"

UBOOT_GIT_URI ?= "git://github.com/analogdevicesinc/u-boot-sharc.git"
UBOOT_GIT_PROTOCOL = "https"

WATCHDOG_PATCH = "${@ 'file://enable-ADI-watchdog.patch' if d.getVar('ANALOG_DEVICES_WATCHDOG') else ''}"

SRC_URI += " \
	${UBOOT_GIT_URI};protocol=${UBOOT_GIT_PROTOCOL};branch=${UBOOT_BRANCH} \
    ${WATCHDOG_PATCH} \
"

PV_append = "+git${SRCPV}"

DEPENDS += "dtc-native bc-native lzop-native"

PROVIDES += "u-boot"
PKG_${PN} = "u-boot"
PKG_${PN}-dev = "u-boot-dev"
PKG_${PN}-dbg = "u-boot-dbg"

S = "${WORKDIR}/git"
