DESCRIPTION = "RPMsg-List Echo Example program for ADI"
LICENSE = "CLOSED"

SRC_URI += " \
	file://echo_core1.ldr \
	file://echo_core2.ldr \
"

do_install() {
	install -m 0755 -d ${D}/lib/firmware
	install -m 0755 ${WORKDIR}/echo_core1.ldr ${D}/lib/firmware
	install -m 0755 ${WORKDIR}/echo_core2.ldr ${D}/lib/firmware
}

FILES_${PN} += " \
	/lib/firmware/echo_core1.ldr \
	/lib/firmware/echo_core2.ldr \
"
