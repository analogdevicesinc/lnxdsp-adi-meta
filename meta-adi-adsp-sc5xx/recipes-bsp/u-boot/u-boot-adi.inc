SUMMARY = "U-Boot For ADSP-SC5xx Products"

inherit analog-devices-baremetal autotools

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot-adi:"

def get_lic_files_chksum (d):
  MACHINE = d.getVar('MACHINE')
  if MACHINE == 'adsp-sc589-ezkit' or MACHINE == 'adsp-sc573-ezkit':
    LIC_FILES_CHKSUM = "file://Licenses/README;md5=5a7450c57ffe5ae63fd732446b988025"
  else:
    LIC_FILES_CHKSUM = "file://Licenses/README;md5=c7383a594871c03da76b3707929d2919"
  return LIC_FILES_CHKSUM

def get_uboot_branch (d):
  MACHINE = d.getVar('MACHINE')
  if MACHINE == 'adsp-sc589-ezkit' or MACHINE == 'adsp-sc573-ezkit':
    UBOOT_BRANCH = "master"
  else:
    UBOOT_BRANCH = "release/yocto-1.0.0"
  return UBOOT_BRANCH

#UBOOT_GIT_URI ?= "git://github.com/analogdevicesinc/u-boot-sharc.git"
#UBOOT_GIT_PROTOCOL ?= "https"

def get_git_uri (d):
  #Pull in from Timesys repository for now.  Change this when releasing SC594 board to public
  MACHINE = d.getVar('MACHINE')
  if MACHINE == 'adsp-sc589-ezkit' or MACHINE == 'adsp-sc573-ezkit':
    UBOOT_GIT_URI = "git://git@src.timesys.com/services/analog-devices/analog-devices-new-board-bringup-1/u-boot-sharc-2020.10.git"
  else:
    UBOOT_GIT_URI = "git://git@src.timesys.com/services/analog-devices/u-boot-2020.10"
  return UBOOT_GIT_URI

UBOOT_GIT_PROTOCOL = "ssh"

def get_wdt_patch (d):
  if d.getVar('ANALOG_DEVICES_WATCHDOG'):
    MACHINE = d.getVar('MACHINE')
    if MACHINE == 'adsp-sc594-som-ezkit':
      WDT_FILE = "file://enable-ADI-watchdog-sc594.patch"
    elif MACHINE == 'adsp-sc589-ezkit' or MACHINE == 'adsp-sc573-ezkit':
      WDT_FILE = "file://enable-ADI-watchdog-2020.10.patch"
    else:
      WDT_FILE = "file://enable-ADI-watchdog.patch"
  return WDT_FILE

SRC_URI += " \
	${@get_git_uri(d)};protocol=${UBOOT_GIT_PROTOCOL};branch=${@get_uboot_branch(d)} \
	${@get_wdt_patch(d)} \
"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "${@get_lic_files_chksum(d)}"

PV_append = "+git${SRCPV}"

DEPENDS += "dtc-native bc-native lzop-native bison-native"

PROVIDES += "u-boot"
PKG_${PN} = "u-boot"
PKG_${PN}-dev = "u-boot-dev"
PKG_${PN}-dbg = "u-boot-dbg"

S = "${WORKDIR}/git"
