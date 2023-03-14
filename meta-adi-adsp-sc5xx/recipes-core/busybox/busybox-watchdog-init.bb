FILESEXTRAPATHS:prepend := "${THISDIR}/busybox:"

LICENSE = "CLOSED"

INITSCRIPT_NAME = "watchdog.sh"
INITSCRIPT_PARAMS = "start 00 1 2 3 4 5 ."

SYSTEMD_SERVICE_NAME = "${@ 'file://watchdog.service' if bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d) else ''}"
INHERIT_SYSTEMD = "${@ 'systemd' if bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d) else ''}"

SRC_URI += " \
    ${SYSTEMD_SERVICE_NAME} \
    file://${INITSCRIPT_NAME} \
"

inherit update-rc.d ${INHERIT_SYSTEMD}

do_install(){

    SYSTEMD="${@ '1' if bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d) else '0'}"


    if [ "${SYSTEMD}" = "1" ]; then
        install -d ${D}${libexecdir}
        install -m 0755 ${WORKDIR}/${INITSCRIPT_NAME} ${D}${libexecdir}/${INITSCRIPT_NAME}
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/watchdog.service ${D}${systemd_system_unitdir}/watchdog.service

        install -d ${D}/etc/systemd/system/multi-user.target.wants/
        ln -s ${systemd_system_unitdir}/watchdog.service} ${D}/etc/systemd/system/multi-user.target.wants/watchdog.service
    else
        install -d ${D}/${INIT_D_DIR}
        install -m 0755 ${WORKDIR}/${INITSCRIPT_NAME} ${D}/${INIT_D_DIR}/${INITSCRIPT_NAME}
    fi
}

SYSTEMD_INITSCRIPT_NAME_FILES = "${@ '${libexecdir}/${INITSCRIPT_NAME}' if bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d) else ''}"

SYSTEMD_SERVICE_NAME_FILES = "${@ '${systemd_system_unitdir}/watchdog.service' if bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d) else ''}"

FILES:${PN} += " \
    ${SYSTEMD_INITSCRIPT_NAME_FILES} \
    ${SYSTEMD_SERVICE_NAME_FILES} \
    ${SYSVINIT_FILES} \
"