DESCRIPTION = "ADI PINT test utility"
LICENSE = "CLOSED"

SRC_URI = "file://pint.c"

FILES:${PN} = "/usr/bin/test_pint"

S = "${WORKDIR}/pint_test"

do_compile() {
	${CC} ${CFLAGS} ${WORKDIR}/pint.c -c -o ${S}/pint.o
	${CC} ${LDFLAGS} ${S}/pint.o -o ${S}/test_pint
}

do_install() {
	install -d ${D}/usr/bin
	install -m 0755 ${S}/test_pint ${D}/usr/bin/test_pint
}
