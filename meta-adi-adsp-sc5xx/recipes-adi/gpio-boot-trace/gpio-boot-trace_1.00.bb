SUMMARY = "Boot-time GPIO trace marker"
LICENSE = "CLOSED"

inherit systemd

RDEPENDS:${PN} = "libgpiod-tools"

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "gpio-boot-trace.service"

SRC_URI = "file://gpio-boot-trace.service file://gpio-boot-trace.sh"

FILES:${PN} += "${systemd_unitdir}/system/gpio-boot-trace.service"

do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/gpio-boot-trace.service ${D}${systemd_unitdir}/system

    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/gpio-boot-trace.sh ${D}${bindir}
}
