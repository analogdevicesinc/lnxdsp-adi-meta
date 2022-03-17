DESCRIPTION = "Remote Proc Examples for ADI"
LICENSE = "CLOSED"

SRC_URI += " \
	file://mcapi_send_recv_sc589_Core1.ldr \
	file://mcapi_send_recv_sc589_Core2.ldr \
	file://Toggle_LED_GPIO_SC589_SHARC_Core1.ldr \
	file://Toggle_LED_GPIO_SC589_SHARC_Core2.ldr \
	file://load_mcapi.sh \
	file://load_led_blink.sh \
"

do_install() {
	install -m 0755 -d ${D}/remoteproc
	install -m 0755 ${WORKDIR}/*.ldr ${D}/remoteproc/
	install -m 0755 ${WORKDIR}/*.sh ${D}/remoteproc/
}

FILES_${PN} += " \
	/remoteproc/load_mcapi.sh \
	/remoteproc/load_led_blink.sh \
	/remoteproc/mcapi_send_recv_sc589_Core1.ldr \
	/remoteproc/mcapi_send_recv_sc589_Core2.ldr \
	/remoteproc/Toggle_LED_GPIO_SC589_SHARC_Core1.ldr \
	/remoteproc/Toggle_LED_GPIO_SC589_SHARC_Core2.ldr \
"

RDEPENDS_${PN} = " libmcapi sc5xx-corecontrol "
