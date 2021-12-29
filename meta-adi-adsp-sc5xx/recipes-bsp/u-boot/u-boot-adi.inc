SUMMARY = "U-Boot For ADSP-SC5xx Products"

inherit autotools

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot-adi:"

def get_uboot_branch (d):
  MACHINE = d.getVar('MACHINE')
  if MACHINE == 'adsp-sc598-som-ezkit':
    UBOOT_BRANCH = "develop/sc598"
  else:
    UBOOT_BRANCH = "master"
  return UBOOT_BRANCH

def get_git_uri (d):
  #Pull in from Timesys repository for now.  Change this when releasing SC594 board to public
  #UBOOT_GIT_URI ?= "git://github.com/analogdevicesinc/u-boot-sharc.git"
  UBOOT_GIT_URI = "git://git@src.timesys.com/services/analog-devices/analog-devices-new-board-bringup-1/u-boot-sharc-2020.10.git"
  return UBOOT_GIT_URI

#UBOOT_GIT_PROTOCOL ?= "https"
UBOOT_GIT_PROTOCOL = "ssh"

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
	${@get_git_uri(d)};protocol=${UBOOT_GIT_PROTOCOL};branch=${@get_uboot_branch(d)} \
	${@get_wdt_patch(d)} ${@get_ramdisk_patch(d)} \
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

# overwrite with another signature type in local.conf if you prefer
# valid options are:
# BLp - plaintext with authentication
# BLw - encrypted in wrapped key format
# BLx - encrypted in keyless format
ADI_SIGNATURE_TYPE ?= "BLp"

SIGNTOOL_PROC = "ADSP-SC594"
SIGNTOOL_PROC_adsp-sc598-som-ezkit = "ADSP-SC598"
