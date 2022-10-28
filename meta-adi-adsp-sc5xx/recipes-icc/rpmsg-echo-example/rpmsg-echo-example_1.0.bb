DESCRIPTION = "RPMsg-List Echo Example program for ADI"
LICENSE = "CLOSED"

SRC_URI += " \
	file://echo_core1-${MACHINE}.ldr \
	file://echo_core2-${MACHINE}.ldr \
	file://test_rpmsg_echo.sh \
"

do_install() {
	install -m 0755 -d ${D}/lib/firmware
	install -m 0755 -d ${D}/usr/bin
	install -m 0755 ${WORKDIR}/echo_core1-${MACHINE}.ldr ${D}/lib/firmware/echo_core1.ldr
	install -m 0755 ${WORKDIR}/echo_core2-${MACHINE}.ldr ${D}/lib/firmware/echo_core2.ldr
	install -m 0755 ${WORKDIR}/test_rpmsg_echo.sh ${D}/usr/bin
}

FILES_${PN} += " \
	/lib/firmware/echo_core1.ldr \
	/lib/firmware/echo_core2.ldr \
	/usr/bin/test_rpmsg_echo.sh \
"
