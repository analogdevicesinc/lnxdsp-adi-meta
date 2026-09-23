FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append = " file://90-override.conf file://watchdog.conf file://console.conf"

do_install:append() {
	install -d ${D}/usr/lib/sysctl.d
	install -m 0644 ${UNPACKDIR}/90-override.conf ${D}/usr/lib/sysctl.d

	install -d ${D}${systemd_unitdir}/system.conf.d
	install -m 0644 ${UNPACKDIR}/watchdog.conf ${D}${systemd_unitdir}/system.conf.d

	install -d ${D}${systemd_system_unitdir}/serial-getty@ttySC0.service.d
	install -m 0644 ${UNPACKDIR}/console.conf ${D}${systemd_system_unitdir}/serial-getty@ttySC0.service.d/10-adsp-console.conf
}
