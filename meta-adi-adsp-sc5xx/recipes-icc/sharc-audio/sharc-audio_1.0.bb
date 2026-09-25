DESCRIPTION = "Binaries for SHARC Audio demos"
LICENSE = "CLOSED"

S = "${UNPACKDIR}"

SRC_URI += " \
	file://icap-sharc-alsa_Core1.ldr \
	file://icap-sharc-alsa_Core2.ldr \
"

do_install() {
	install -m 0755 -d ${D}${nonarch_base_libdir}/firmware
	install -m 0755 ${UNPACKDIR}/icap-sharc-alsa_Core1.ldr ${D}${nonarch_base_libdir}/firmware/adi_adsp_core1_fw.ldr
	install -m 0755 ${UNPACKDIR}/icap-sharc-alsa_Core2.ldr ${D}${nonarch_base_libdir}/firmware/adi_adsp_core2_fw.ldr	
}

FILES:${PN} = " \
	${nonarch_base_libdir}/firmware/adi_adsp_core1_fw.ldr \
	${nonarch_base_libdir}/firmware/adi_adsp_core2_fw.ldr \
"
