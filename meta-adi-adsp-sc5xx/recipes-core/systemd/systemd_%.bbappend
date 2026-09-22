FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append = " file://90-override.conf file://watchdog.conf"

do_install:append() {
	install -d ${D}/usr/lib/sysctl.d
	install -m 0644 ${UNPACKDIR}/90-override.conf ${D}/usr/lib/sysctl.d

	install -d ${D}${systemd_unitdir}/system.conf.d
	install -m 0644 ${UNPACKDIR}/watchdog.conf ${D}${systemd_unitdir}/system.conf.d
}
