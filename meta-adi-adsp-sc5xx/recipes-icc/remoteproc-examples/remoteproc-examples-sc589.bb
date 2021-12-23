DESCRIPTION = "Remote Proc Examples for ADI"
LICENSE = "CLOSED"

SRC_URI += " \
	file://mcapi_send_recv_Core1_sc589.ldr \
	file://load_mcapi.sh \
"

do_install() {
	install -m 0755 -d ${D}/remoteproc
	install -m 0755 ${WORKDIR}/mcapi_send_recv_Core1_sc589.ldr ${D}/remoteproc/
	install -m 0755 ${WORKDIR}/load_mcapi.sh ${D}/remoteproc/
}

FILES_${PN} += " \
	/remoteproc/load_mcapi.sh \
	/remoteproc/mcapi_send_recv_Core1_sc589.ldr \
"
