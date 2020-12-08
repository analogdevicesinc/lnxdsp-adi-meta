DESCRIPTION = "Remote Proc Examples for ADI"
LICENSE = "CLOSED"

SRC_URI += " \
	file://mcapi_send_recv_Core1_sc594.ldr \
	file://mcapi_send_recv_Core2_sc594.ldr \
	file://LED_Blink_SC594_SHARC_Core1.ldr \
	file://LED_Blink_SC594_SHARC_Core2.ldr \
	file://load_led_blink.sh \
	file://load_mcapi.sh \
"

do_install() {
	install -m 0755 -d ${D}/remoteproc
	install -m 0755 ${WORKDIR}/LED_Blink_SC594_SHARC_Core1.ldr ${D}/remoteproc/
	install -m 0755 ${WORKDIR}/LED_Blink_SC594_SHARC_Core2.ldr ${D}/remoteproc/
	install -m 0755 ${WORKDIR}/mcapi_send_recv_Core1_sc594.ldr ${D}/remoteproc/
	install -m 0755 ${WORKDIR}/mcapi_send_recv_Core2_sc594.ldr ${D}/remoteproc/
	install -m 0755 ${WORKDIR}/load_led_blink.sh ${D}/remoteproc/
	install -m 0755 ${WORKDIR}/load_mcapi.sh ${D}/remoteproc/
}

FILES_${PN} += " \
	/remoteproc/load_led_blink.sh \
	/remoteproc/LED_Blink_SC594_SHARC_Core2.ldr \
	/remoteproc/LED_Blink_SC594_SHARC_Core1.ldr \
	/remoteproc/load_mcapi.sh \
	/remoteproc/mcapi_send_recv_Core1_sc594.ldr \
	/remoteproc/mcapi_send_recv_Core2_sc594.ldr \
"
