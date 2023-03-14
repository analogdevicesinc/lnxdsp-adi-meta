DESCRIPTION = "Binaries for SHARC Audio demos"
LICENSE = "CLOSED"

SRC_URI += " \
	file://icap-sharc-alsa_Core1.ldr \
	file://icap-sharc-alsa_Core2.ldr \
"

do_install() {
	install -m 0755 -d ${D}/lib/firmware
	install -m 0755 ${WORKDIR}/icap-sharc-alsa_Core1.ldr ${D}/lib/firmware/adi_adsp_core1_fw.ldr
	install -m 0755 ${WORKDIR}/icap-sharc-alsa_Core2.ldr ${D}/lib/firmware/adi_adsp_core2_fw.ldr	
}

FILES:${PN} = " \
	/lib/firmware/adi_adsp_core1_fw.ldr \
	/lib/firmware/adi_adsp_core2_fw.ldr \
"
