DESCRIPTION = "Binaries for SHARC Audio demos"
LICENSE = "CLOSED"

SRC_URI += " \
	file://icap-device-example_Core1.ldr \
	file://icap-sharc-alsa_Core1.ldr \
	file://icap-sharc-alsa_Core2.ldr \
"

do_install() {
	install -m 0755 -d ${D}/lib/firmware
	install -m 0755 ${WORKDIR}/icap-device-example_Core1.ldr ${D}/lib/firmware
	install -m 0755 ${WORKDIR}/icap-sharc-alsa_Core1.ldr ${D}/lib/firmware
	install -m 0755 ${WORKDIR}/icap-sharc-alsa_Core2.ldr ${D}/lib/firmware
}

FILES_${PN} = " \
	/lib/firmware/icap-device-example_Core1.ldr \
	/lib/firmware/icap-sharc-alsa_Core1.ldr \
	/lib/firmware/icap-sharc-alsa_Core2.ldr \
"
