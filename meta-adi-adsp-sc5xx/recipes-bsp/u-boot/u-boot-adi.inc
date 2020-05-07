SUMMARY = "U-Boot For ADSP-SC5xx Products"

inherit analog-devices-baremetal autotools

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot-adi:"

UBOOT_GIT_URI = "https://github.com/analogdevicesinc/u-boot-sharc.git"
UBOOT_GIT_PROTOCOL = "https"

#No longer used, automatic detection
#ETHERNET_PATCH = "${@ 'file://sc589-ezkit-2.0-eth-uboot.patch' if d.getVar('ANALOG_DEVICES_VERSION2_ETHERNET') else ''}"
#SRC_URI += "${ETHERNET_PATCH}""

WATCHDOG_PATCH = "${@ 'file://enable-ADI-watchdog.patch' if d.getVar('ANALOG_DEVICES_WATCHDOG') else ''}"

SRC_URI += " \
	${UBOOT_GIT_URI};protocol=${UBOOT_GIT_PROTOCOL};branch=${BRANCH} \
    file://0001-drop-configh-from-tools.patch \
    file://0002-uboot-tools-nommu.patch \
    file://0003-Boot-Sequence-Changes.patch \
    ${WATCHDOG_PATCH} \
"

PV_append = "+git${SRCPV}"

DEPENDS += "dtc-native bc-native lzop-native"

PROVIDES += "u-boot"
PKG_${PN} = "u-boot"
PKG_${PN}-dev = "u-boot-dev"
PKG_${PN}-dbg = "u-boot-dbg"

S = "${WORKDIR}/git"
