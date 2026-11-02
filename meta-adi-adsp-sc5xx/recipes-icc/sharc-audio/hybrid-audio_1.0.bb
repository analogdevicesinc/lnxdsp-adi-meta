DESCRIPTION = "Binaries for SHARC Audio demos"
LICENSE = "CLOSED"

S = "${UNPACKDIR}"

SRC_URI += " \
	file://icap-device-example_Core1.ldr \
"

do_install() {
	install -m 0755 -d ${D}${nonarch_base_libdir}/firmware/adi
	install -m 0755 ${UNPACKDIR}/icap-device-example_Core1.ldr ${D}${nonarch_base_libdir}/firmware/adi/icap_device_example_core1.ldr
}

FILES:${PN} = " \
	${nonarch_base_libdir}/firmware/adi/icap_device_example_core1.ldr \
"
