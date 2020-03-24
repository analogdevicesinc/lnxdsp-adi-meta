require linux-adi.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

PR = "r0"

SRCBRANCH = "develop/yocto-1.0.0"
SRCREV  = "${AUTOREV}"

SRAM_ALLOC_PATCH = "${@ 'file://Add-l2-alloc-module.patch' if d.getVar('ANALOG_DEVICES_SRAM_ALLOC') else ''}"

SRC_URI += " \
	file://0001-Fix-errors-for-compiling-with-GCC8.patch \
	file://0002-Add-NFS-client-file-system-support-in-to-4.16-kernel.patch \
	file://0004-Add-in-ADI-Hardware-CRC-crypto-driver.patch \
	file://0005-Add-in-ADAU1962-ADAU1979-as-built-ins-instead-of-mod.patch \
	file://0011-Add-in-wireless-support.patch \	
	file://0012-Add-in-video-encoder-decoders.patch \
	file://0013-Add-in-Gadget-Audio-and-Gadget-Zero-drivers.patch \
	${SRAM_ALLOC_PATCH} \
"
