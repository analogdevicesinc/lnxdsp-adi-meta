DESCRIPTION = "RPMsg-List Echo Example program for ADI"
LICENSE = "CLOSED"

# The echo firmware is built per machine. For the sc846 all we've got is
# a copy of the sc598 SHARC+ image, which won't run on the SHARC-FX core -
# it's the wrong ISA and just faults if you load it. So skip the firmware
# on sc846 until theres a proper SHARC-FX build, but keep shipping the test
# script like everywhere else.
ECHO_FW = " file://echo_core1-${MACHINE}.ldr file://echo_core2-${MACHINE}.ldr"
ECHO_FW:adsp-sc846-som-ezkit = ""

ECHO_FW_FILES = " /usr/lib/firmware/adi_adsp_core1_fw.ldr /usr/lib/firmware/adi_adsp_core2_fw.ldr"
ECHO_FW_FILES:adsp-sc846-som-ezkit = ""

SRC_URI += " \
	${ECHO_FW} \
	file://test_rpmsg_echo.sh \
"

do_install() {
	install -m 0755 -d ${D}/usr/bin
	install -m 0755 ${WORKDIR}/test_rpmsg_echo.sh ${D}/usr/bin

	if [ -n "${ECHO_FW}" ]; then
		install -m 0755 -d ${D}/usr/lib/firmware
		install -m 0755 ${WORKDIR}/echo_core1-${MACHINE}.ldr ${D}/usr/lib/firmware/adi_adsp_core1_fw.ldr
		install -m 0755 ${WORKDIR}/echo_core2-${MACHINE}.ldr ${D}/usr/lib/firmware/adi_adsp_core2_fw.ldr
	fi
}

FILES:${PN} += " \
	${ECHO_FW_FILES} \
	/usr/bin/test_rpmsg_echo.sh \
"
