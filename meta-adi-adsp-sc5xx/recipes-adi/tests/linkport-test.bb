DESCRIPTION = "Analog Devices Linkport Test"
LICENSE = "CLOSED"

SRC_URI = " \
	file://source/linkport_test.c \
"

S = "${WORKDIR}/source"

do_compile(){
	cd ${S}
	${CC} ${CFLAGS} ${LDFLAGS} linkport_test.c -o linkport_test
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/linkport_test ${D}/usr/bin/
}