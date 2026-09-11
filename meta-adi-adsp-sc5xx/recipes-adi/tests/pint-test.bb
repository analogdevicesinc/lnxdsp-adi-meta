DESCRIPTION = "ADI PINT test utility"
LICENSE = "CLOSED"

SRC_URI = "file://pint.c"

FILES:${PN} = "/usr/bin/test_pint"

S = "${UNPACKDIR}"
B = "${WORKDIR}/build"

do_compile() {
	${CC} ${CFLAGS} ${S}/pint.c -c -o ${B}/pint.o
	${CC} ${LDFLAGS} ${B}/pint.o -o ${B}/test_pint
}

do_install() {
	install -d ${D}/usr/bin
	install -m 0755 ${B}/test_pint ${D}/usr/bin/test_pint
}
