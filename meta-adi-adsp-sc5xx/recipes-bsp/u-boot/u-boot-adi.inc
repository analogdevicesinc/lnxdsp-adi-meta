SUMMARY = "U-Boot For ADSP-SC5xx Products"

inherit autotools

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot-adi:"

def get_wdt_patch (d):
  WDT_FILE = ""
  if d.getVar('ANALOG_DEVICES_WATCHDOG'):
    MACHINE = d.getVar('MACHINE')
    if MACHINE == 'adsp-sc594-som-ezkit':
      WDT_FILE = "file://enable-ADI-watchdog-sc594.patch"
    elif MACHINE == 'adsp-sc589-ezkit' or MACHINE == 'adsp-sc573-ezkit':
      WDT_FILE = "file://enable-ADI-watchdog-2020.10.patch"
    else:
      WDT_FILE = "file://enable-ADI-watchdog.patch"
  return WDT_FILE

def get_ramdisk_patch (d):
  MACHINE = d.getVar('MACHINE')
  if MACHINE == 'adsp-sc594-som-ezkit':
    return " file://disable-relocation-of-ramdisk-to-top-of-memory.patch"
  else:
    return ""

SRC_URI += " \
	${UBOOT_GIT_URI};protocol=${UBOOT_GIT_PROTOCOL};branch=${UBOOT_BRANCH} \
	${@get_wdt_patch(d)} ${@get_ramdisk_patch(d)} \
"

LICENSE = "GPLv2+"

PV_append = "+git${SRCPV}"

DEPENDS += "dtc-native bc-native lzop-native bison-native"

PROVIDES += "u-boot"
PKG_${PN} = "u-boot"
PKG_${PN}-dev = "u-boot-dev"
PKG_${PN}-dbg = "u-boot-dbg"

S = "${WORKDIR}/git"
