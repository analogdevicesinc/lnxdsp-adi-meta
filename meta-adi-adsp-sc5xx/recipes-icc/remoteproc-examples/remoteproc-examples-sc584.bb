DESCRIPTION = "Remote Proc Examples for ADI"
LICENSE = "CLOSED"

SRC_URI += " \
	file://mcapi_send_recv_sc584_Core1.ldr \
	file://mcapi_send_recv_sc584_Core2.ldr \
	file://load_mcapi.sh \
"

do_install() {
	install -m 0755 -d ${D}/remoteproc
	install -m 0755 ${WORKDIR}/*.ldr ${D}/remoteproc/
	install -m 0755 ${WORKDIR}/*.sh ${D}/remoteproc/
}

FILES_${PN} += " \
	/remoteproc/load_mcapi.sh \
	/remoteproc/mcapi_send_recv_sc584_Core1.ldr \
	/remoteproc/mcapi_send_recv_sc584_Core2.ldr \
"
