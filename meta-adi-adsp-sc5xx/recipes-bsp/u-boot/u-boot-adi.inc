SUMMARY = "U-Boot For ADSP-SC5xx Products"

inherit autotools

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-adi:"

UBOOT_GIT_URI ?= "git://github.com/analogdevicesinc/lnxdsp-u-boot.git"
UBOOT_GIT_PROTOCOL ?= "https"

UBOOT_WDT_FILE = "file://enable-ADI-watchdog.patch"
UBOOT_WDT = "${@bb.utils.contains('MACHINE_FEATURES', 'watchdog', '${UBOOT_WDT_FILE}', '', d)}"

UBOOT_BRANCH ?= "develop/yocto-3.1.0"

# On the SC598, we can't boot directly from the eMMC due to default speed configuration issues
# We can boot the SPL from OSPI or QSPI and then subsequently boot U-Boot Proper from eMMC
# To accomplish this, we need to set CONFIG_ADI_SPL_FORCE_BMODE=6 in u-boot.
BMODE_FORCE_EMMC = "${@bb.utils.contains('DISTRO_FEATURES', \
                   'bmode_force_emmc', 'file://bmode_force_emmc.cfg', '', d)}"

SRC_URI += " \
	${UBOOT_GIT_URI};protocol=${UBOOT_GIT_PROTOCOL};branch=${UBOOT_BRANCH} \
	${UBOOT_WDT} \
	${BMODE_FORCE_EMMC} \
"

SRC_URI:append:adsp-sc598-som-ezkit = "${@' file://0001-sc598-som-enable-SDcard.patch' if bb.utils.to_boolean(d.getVar('ADSP_SC598_SDCARD')) else ''}"

SRC_URI:append = "${@' file://0002-developer-mode-enable.patch' if bb.utils.to_boolean(d.getVar('DEVELOPER_MODE')) else '' }"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

DEPENDS += "dtc-native bc-native lzop-native bison-native ldr-adi-native"

PROVIDES += "u-boot"
PKG:${PN} = "u-boot"
PKG:${PN}-dev = "u-boot-dev"
PKG:${PN}-dbg = "u-boot-dbg"

S = "${WORKDIR}/git"

UBOOT_MKIMAGE_DTCOPTS ?= "-I dts -O dtb -p 2000"
