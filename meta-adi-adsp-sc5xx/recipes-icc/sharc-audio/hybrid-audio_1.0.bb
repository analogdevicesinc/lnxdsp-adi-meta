DESCRIPTION = "Binaries for SHARC Audio demos"
LICENSE = "CLOSED"

S = "${UNPACKDIR}"

SRC_URI += " \
	file://icap-device-example_Core1.ldr \
"

do_install() {
	install -m 0755 -d ${D}${nonarch_base_libdir}/firmware
	install -m 0755 ${UNPACKDIR}/icap-device-example_Core1.ldr ${D}${nonarch_base_libdir}/firmware/adi_adsp_core1_fw.ldr
}

FILES:${PN} = " \
	${nonarch_base_libdir}/firmware/adi_adsp_core1_fw.ldr \
"
