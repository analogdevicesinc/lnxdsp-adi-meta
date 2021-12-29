DESCRIPTION = "adi signtool used for code signing"
LICENSE = "CLOSED"

DEPENDS = "openssl-native"

inherit native

SRC_URI = " \
	file://adi_signtool \
	file://signtool \
"

do_install() {
	install -d ${D}${bindir}/
	install -m 0755 ${WORKDIR}/adi_signtool ${D}${bindir}
	install -m 0755 ${WORKDIR}/signtool ${D}${bindir}
}

FILES_${PN} += " \
	${bindir}/adi_signtool \
	${bindir}/signtool \
"

INSANE_SKIP_${PN} = "already-stripped"
