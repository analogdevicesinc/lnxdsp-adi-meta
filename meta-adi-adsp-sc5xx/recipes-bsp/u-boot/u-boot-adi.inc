SUMMARY = "U-Boot For ADSP-SC5xx Products"

inherit autotools

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot-adi:"

UBOOT_GIT_URI ?= "git://github.com/analogdevicesinc/lnxdsp-u-boot.git"

UBOOT_GIT_PROTOCOL ?= "https"

UBOOT_WDT_FILE = "file://enable-ADI-watchdog.patch"
UBOOT_WDT_FILE_adsp-sc589-ezkit = "file://enable-ADI-watchdog-2020.10.patch"
UBOOT_WDT_FILE_adsp-sc573-ezkit = "file://enable-ADI-watchdog-2020.10.patch"
UBOOT_WDT_FILE_adsp-sc594-ezkit = "file://enable-ADI-watchdog-sc594.patch"
UBOOT_WDT = "${@bb.utils.contains('MACHINE_FEATURES', 'watchdog', '${UBOOT_WDT_FILE}', '', d)}"

UBOOT_RAMDISK_PATCH = ""
UBOOT_RAMDISK_PATCH_adsp-sc594-som-ezkit = " file://disable-relocation-of-ramdisk-to-top-of-memory.patch"

UBOOT_BRANCH ?= "master"
UBOOT_BRANCH_adsp-sc598-som-ezkit ?= "release/2.1.0"

SRC_URI += " \
	${UBOOT_GIT_URI};protocol=${UBOOT_GIT_PROTOCOL};branch=${UBOOT_BRANCH} \
	${UBOOT_WDT} ${UBOOT_RAMDISK_PATCH} \
"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=5a7450c57ffe5ae63fd732446b988025"

PV_append = "+git${SRCPV}"

DEPENDS += "dtc-native bc-native lzop-native bison-native ldr-adi-native"

PROVIDES += "u-boot"
PKG_${PN} = "u-boot"
PKG_${PN}-dev = "u-boot-dev"
PKG_${PN}-dbg = "u-boot-dbg"

S = "${WORKDIR}/git"

UBOOT_MKIMAGE_DTCOPTS ?= "-I dts -O dtb -p 2000"
