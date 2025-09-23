FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append = "file://90-override.conf"

do_install:append() {
	install -d ${D}/usr/lib/sysctl.d
	install -m 0644 ${WORKDIR}/90-override.conf ${D}/usr/lib/sysctl.d
}
