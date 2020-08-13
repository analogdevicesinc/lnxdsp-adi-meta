DESCRIPTION = "Analog Devices Fastboot Listener Application"
LICENSE = "CLOSED"

PR = "r0"

SRC_URI = " \
	file://source/fastboot_server.c \
	file://source/fastboot_client.c \
	file://source/fastboot.h \
	"

S = "${WORKDIR}/source"

do_compile(){
	cd ${S}
	${CC} ${CFLAGS} ${LDFLAGS} fastboot_server.c -o fastboot-listener
	gcc ${CFLAGS} fastboot_client.c -o fastboot_client
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/fastboot-listener ${D}/usr/bin/
}
