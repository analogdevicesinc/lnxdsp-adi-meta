SUMMARY = "PTP 1588 clock support"
DESCRIPTION = "This binary provide PTP 1588 clock support for ADI sc5xx boards"

FILESEXTRAPATHS_prepend := "${THISDIR}/linuxptp-test:"

SRC_URI += " file://0001-Add-linuxptp-adi-patch-for-testptp.patch \
"

do_compile_append() {
	cd ${S}
	${CC} ${CFLAGS} ${LDFLAGS} testptp.c -o testptp -lrt
}

do_install_append() {
	install -d ${D}/usr/bin
	install -d ${D}${sysconfdir}
	install -m 755 ${S}/testptp ${D}/usr/bin/
	install -m 755 ${S}/configs/default.cfg ${D}${sysconfdir}/ptp4l.cfg
}
