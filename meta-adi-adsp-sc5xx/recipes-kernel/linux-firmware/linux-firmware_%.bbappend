SUMMARY = "Linux kernel firmware files from ADI distribution"
DESCRIPTION = "These binaries provide kernel support for ADI sc5xx boards"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-firmware:"
SRC_URI += "file://adau1761.bin \
			file://LICENSE.adau1761 \
			"

LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE.adau1761;md5=dff5777c9526c7f6db0a0571f066e818"

FILEPATH = "/lib/firmware"

do_install_append() {
	install -m 0644 ${WORKDIR}/LICENSE.adau1761 ${D}${FILEPATH}
	install -m 0644 ${WORKDIR}/adau1761.bin ${D}${FILEPATH}
}

PACKAGES =+ "${PN}-adau1761"

FILES_${PN}-adau1761 = " \
		${FILEPATH}/LICENSE.adau1761 \
		${FILEPATH}/adau1761.bin \
		"
