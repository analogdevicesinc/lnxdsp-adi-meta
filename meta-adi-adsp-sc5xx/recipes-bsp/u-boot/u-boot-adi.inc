SUMMARY = "U-Boot For ADSP-SC5xx Products"

inherit autotools

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-adi:${THISDIR}/u-boot-adi/debug-cfg:"

UBOOT_GIT_URI ?= "git://github.com/analogdevicesinc/u-boot.git"
UBOOT_GIT_PROTOCOL ?= "https"

UBOOT_WDT_FILE = "file://enable-ADI-watchdog.patch"
UBOOT_WDT = "${@bb.utils.contains('MACHINE_FEATURES', 'watchdog', '${UBOOT_WDT_FILE}', '', d)}"

UBOOT_SOM_FILE = ""
UBOOT_SOM_FILE:adsp-sc598-som-ezkit = "file://0001-SC598-SOM-Adding-Rev-D-compatibility-for-u-boot.patch"
UBOOT_SOM = "${@bb.utils.contains('SOM_REV', 'D', '${UBOOT_SOM_FILE}', '', d)}"

python() {
    d.setVar('SDCARD_PATCH', '')
    d.setVar('USB_PATCH', '')

    if d.getVar('SOM_REV') != 'D':
        d.setVar('SDCARD_PATCH', ' file://0001-sc598-som-enable-SD-card-revE.patch')
    else:
        d.setVar('SDCARD_PATCH', ' file://0001-sc598-som-enable-SD-card.patch')

    machine = d.getVar('MACHINE')
    if machine in ('adsp-sc598-som-ezkit', 'adsp-sc594-som-ezkit'):
        d.setVar('USB_PATCH', ' file://0001-Add-USB-support-for-sc594-and-sc598.patch file://0002-Disable-eth1-to-enable-EZKIT-USB.patch')
    elif machine in ('adsp-sc598-som-ezlite', 'adsp-sc594-som-ezlite'):
        d.setVar('USB_PATCH', ' file://0001-Add-USB-support-for-sc594-and-sc598.patch')

}

UBOOT_BRANCH ?= "adi-u-boot-2025.10.y"

# On the SC598, we can't boot directly from the eMMC due to default speed configuration issues
# We can boot the SPL from OSPI or QSPI and then subsequently boot U-Boot Proper from eMMC
# To accomplish this, we need to set CONFIG_ADI_SPL_FORCE_BMODE=6 in u-boot.
BMODE_FORCE_EMMC = "${@bb.utils.contains('DISTRO_FEATURES', \
                   'bmode_force_emmc', 'file://bmode_force_emmc.cfg', '', d)}"

# Debug build option: enable additional debug features (such as storing the
# U-Boot environment in SPI flash).
DEBUG_CFG = ""
DEBUG_CFG:adsp-sc573-ezlite = " file://debug_sc573-ezlite.cfg"
DEBUG_CFG:adsp-sc589-mini = " file://debug_sc589-mini.cfg"
DEBUG_CFG:adsp-sc594-som-ezkit = " file://debug_sc594-som-ezkit.cfg"
DEBUG_CFG:adsp-sc594-som-ezlite = " file://debug_sc594-som-ezlite.cfg"
DEBUG_CFG:adsp-sc598-som-ezkit = " file://debug_sc598-som-ezkit.cfg"
DEBUG_CFG:adsp-sc598-som-ezlite = " file://debug_sc598-som-ezlite.cfg"

SRC_URI += " \
	${UBOOT_GIT_URI};protocol=${UBOOT_GIT_PROTOCOL};branch=${UBOOT_BRANCH} \
	${UBOOT_WDT} \
	${BMODE_FORCE_EMMC} \
        ${UBOOT_SOM} \
"

SRC_URI:append:adsp-sc598-som-ezkit = "${@d.getVar('SDCARD_PATCH') if (bb.utils.to_boolean(d.getVar('ADSP_SC598_SDCARD'))) else ''}"
SRC_URI:append = "${@d.getVar('USB_PATCH') if (bb.utils.to_boolean(d.getVar('ADSP_SC59X_USB'))) else ''}"

SRC_URI:append = "${@' file://0002-developer-mode-enable.patch' if bb.utils.to_boolean(d.getVar('DEVELOPER_MODE')) else '' }"
SRC_URI:append = "${@bb.utils.contains('ADSP_SC5XX_DEBUG', '1', '${DEBUG_CFG}', '', d)}"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

DEPENDS += "dtc-native bc-native lzop-native bison-native ldr-adi-native"

PROVIDES += "u-boot"
PKG:${PN} = "u-boot"
PKG:${PN}-dev = "u-boot-dev"
PKG:${PN}-dbg = "u-boot-dbg"

S = "${WORKDIR}/git"

UBOOT_MKIMAGE_DTCOPTS ?= "-I dts -O dtb -p 2000"
