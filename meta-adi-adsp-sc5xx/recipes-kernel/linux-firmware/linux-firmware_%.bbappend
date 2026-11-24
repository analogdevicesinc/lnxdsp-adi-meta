SUMMARY = "Linux kernel firmware files from ADI distribution"
DESCRIPTION = "These binaries provide kernel support for ADI sc5xx boards"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-firmware:"
SRC_URI += "file://adau1761.bin \
			file://LICENSE.adau1761 \
			file://sharc-alsa/2Ch_L440_R200_48kHz_16bit_6s.wav \
			file://sharc-alsa/LICENSE.md \
			"

SRC_URI[LICENSE.adau1761.md5sum] = "e4c73a481bb7e9ad16049e80f22d669e"
SRC_URI[LICENSE.md.md5sum] = "e4c73a481bb7e9ad16049e80f22d669e"


FILEPATH = "${nonarch_base_libdir}/firmware"

do_install:append() {
        install -m 0644 ${UNPACKDIR}/LICENSE.adau1761 ${D}${FILEPATH}
	install -m 0644 ${UNPACKDIR}/adau1761.bin ${D}${FILEPATH}
	install -m 0644 ${UNPACKDIR}/sharc-alsa/LICENSE.md ${D}${FILEPATH}
	install -d ${D}/usr/share/sounds/alsa/	
	install -m 0644 ${UNPACKDIR}/sharc-alsa/2Ch_L440_R200_48kHz_16bit_6s.wav ${D}/usr/share/sounds/alsa/
}

FILES:${PN} = "${FILEPATH}"

PACKAGES =+ "${PN}-adau1761"

FILES:${PN}-adau1761 = " \
                ${FILEPATH}/LICENSE.adau1761 \
		${FILEPATH}/adau1761.bin \
		"

PACKAGES =+ "${PN}-sharc-alsa"

FILES:${PN}-sharc-alsa = " \
		${FILEPATH}/LICENSE.md \
		/usr/share/sounds/alsa/2Ch_L440_R200_48kHz_16bit_6s.wav \
		"
