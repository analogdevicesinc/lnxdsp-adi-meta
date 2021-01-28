SUMMARY = "U-Boot For ADSP-SC5xx Products"

inherit analog-devices-baremetal autotools

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot-adi:"

#UBOOT_GIT_URI ?= "git://github.com/analogdevicesinc/u-boot-sharc.git"
#UBOOT_GIT_PROTOCOL ?= "https"

#Pull in from Timesys repository for now.  Change this when releasing SC594 board to public
UBOOT_GIT_URI = "git://git@src.timesys.com/services/analog-devices/analog-devices-new-board-bringup-1/u-boot-sharc.git"
UBOOT_GIT_PROTOCOL = "ssh"

def get_wdt_patch (d):
  WDT_FILE = ""
  if d.getVar('ANALOG_DEVICES_WATCHDOG'):
    MACHINE = d.getVar('MACHINE')
    if MACHINE == 'adsp-sc594-som-ezkit':
      WDT_FILE = "file://enable-ADI-watchdog-sc594.patch"
    else:
      WDT_FILE = "file://enable-ADI-watchdog.patch"
  return WDT_FILE

SRC_URI += " \
	${UBOOT_GIT_URI};protocol=${UBOOT_GIT_PROTOCOL};branch=${UBOOT_BRANCH} \
	${@get_wdt_patch(d)} \
"

PV_append = "+git${SRCPV}"

DEPENDS += "dtc-native bc-native lzop-native"

PROVIDES += "u-boot"
PKG_${PN} = "u-boot"
PKG_${PN}-dev = "u-boot-dev"
PKG_${PN}-dbg = "u-boot-dbg"

S = "${WORKDIR}/git"
