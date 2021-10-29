SUMMARY = "Linux kernel firmware files from ADI distribution"
DESCRIPTION = "These binaries provide kernel support for ADI sc5xx boards"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-firmware:"
SRC_URI += "file://adau1761.bin \
			file://LICENSE.adau1761 \
			file://sharc-alsa/2Channel-SC594.ldr \
			file://sharc-alsa/2Ch_L440_R200_48kHz_16bit_6s.wav \
			file://sharc-alsa/LICENSE.md \
			"

SRC_URI[LICENSE.adau1761.md5sum] = "dff5777c9526c7f6db0a0571f066e818"
SRC_URI[LICENSE.md.md5sum] = "e2bfd7246b6d241634f71dfdbfef3d41"


FILEPATH = "/lib/firmware"

do_install_append() {
	install -m 0644 ${WORKDIR}/LICENSE.adau1761 ${D}${FILEPATH}
	install -m 0644 ${WORKDIR}/adau1761.bin ${D}${FILEPATH}
	install -m 0644 ${WORKDIR}/sharc-alsa/LICENSE.md ${D}${FILEPATH}
	install -m 0644 ${WORKDIR}/sharc-alsa/2Channel-SC594.ldr ${D}${FILEPATH}
	install -d ${D}/usr/share/sounds/alsa/	
	install -m 0644 ${WORKDIR}/sharc-alsa/2Ch_L440_R200_48kHz_16bit_6s.wav ${D}/usr/share/sounds/alsa/
}

PACKAGES =+ "${PN}-adau1761"

FILES_${PN}-adau1761 = " \
		${FILEPATH}/LICENSE.adau1761 \
		${FILEPATH}/adau1761.bin \
		"

PACKAGES =+ "${PN}-sharc-alsa"

FILES_${PN}-sharc-alsa = " \
		${FILEPATH}/LICENSE.md \
		${FILEPATH}/2Channel-SC594.ldr \
		/usr/share/sounds/alsa/2Ch_L440_R200_48kHz_16bit_6s.wav \
		"
