DESCRIPTION = "RPMsg-List Echo Example program for ADI"
LICENSE = "CLOSED"

RPMSG_ECHO_MACHINE ?= "${MACHINE}"

SRC_URI += " \
	file://echo_core1-${RPMSG_ECHO_MACHINE}.ldr \
	file://echo_core2-${RPMSG_ECHO_MACHINE}.ldr \
	file://test_rpmsg_echo.sh \
"

do_install() {
	install -m 0755 -d ${D}/usr/lib/firmware
	install -m 0755 -d ${D}/usr/bin
	install -m 0755 ${WORKDIR}/echo_core1-${RPMSG_ECHO_MACHINE}.ldr ${D}/usr/lib/firmware/adi_adsp_core1_fw.ldr
	install -m 0755 ${WORKDIR}/echo_core2-${RPMSG_ECHO_MACHINE}.ldr ${D}/usr/lib/firmware/adi_adsp_core2_fw.ldr
	install -m 0755 ${WORKDIR}/test_rpmsg_echo.sh ${D}/usr/bin
}

FILES:${PN} += " \
	/usr/lib/firmware/adi_adsp_core1_fw.ldr \
	/usr/lib/firmware/adi_adsp_core2_fw.ldr \
	/usr/bin/test_rpmsg_echo.sh \
"
