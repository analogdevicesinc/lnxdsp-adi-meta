FILESEXTRAPATHS_prepend := "${THISDIR}/busybox:"

LICENSE = "CLOSED"

INITSCRIPT_NAME = "watchdog.sh"
INITSCRIPT_PARAMS = "start 05 1 2 3 4 5 ."

SRC_URI += " \
	file://${INITSCRIPT_NAME} \
"

inherit update-rc.d

do_install(){
    install -d ${D}/${INIT_D_DIR}
    install -m 0755 ${WORKDIR}/${INITSCRIPT_NAME} ${D}/${INIT_D_DIR}/${INITSCRIPT_NAME}
}