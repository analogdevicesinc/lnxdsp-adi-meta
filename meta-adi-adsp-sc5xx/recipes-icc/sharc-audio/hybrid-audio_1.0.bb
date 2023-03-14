DESCRIPTION = "Binaries for SHARC Audio demos"
LICENSE = "CLOSED"

SRC_URI += " \
	file://icap-device-example_Core1.ldr \
"

do_install() {
	install -m 0755 -d ${D}/lib/firmware
	install -m 0755 ${WORKDIR}/icap-device-example_Core1.ldr ${D}/lib/firmware/adi_adsp_core1_fw.ldr
}

FILES:${PN} = " \
	/lib/firmware/adi_adsp_core1_fw.ldr \
"
