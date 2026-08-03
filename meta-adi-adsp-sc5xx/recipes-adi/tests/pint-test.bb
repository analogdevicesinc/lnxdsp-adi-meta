DESCRIPTION = "ADI PINT test utility"
LICENSE = "CLOSED"

SRC_URI = "file://pint.c"

FILES:${PN} = "/usr/bin/test_pint"

S = "${UNPACKDIR}"

do_compile() {
	${CC} ${CFLAGS} ${S}/pint.c -c -o ${S}/pint.o
	${CC} ${LDFLAGS} ${S}/pint.o -o ${S}/test_pint
}

do_install() {
	install -d ${D}/usr/bin
	install -m 0755 ${S}/test_pint ${D}/usr/bin/test_pint
}
