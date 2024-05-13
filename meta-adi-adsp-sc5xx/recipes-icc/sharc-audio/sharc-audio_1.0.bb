DESCRIPTION = "Binaries for SHARC Audio demos"
LICENSE = "CLOSED"

SRC_URI += " \
	file://icap-sharc-alsa_Core1.ldr \
	file://icap-sharc-alsa_Core2.ldr \
"

do_install() {
	install -m 0755 -d ${D}/usr/lib/firmware
	install -m 0755 ${WORKDIR}/icap-sharc-alsa_Core1.ldr ${D}/usr/lib/firmware/adi_adsp_core1_fw.ldr
	install -m 0755 ${WORKDIR}/icap-sharc-alsa_Core2.ldr ${D}/usr/lib/firmware/adi_adsp_core2_fw.ldr	
}

FILES:${PN} = " \
	/usr/lib/firmware/adi_adsp_core1_fw.ldr \
	/usr/lib/firmware/adi_adsp_core2_fw.ldr \
"
