DESCRIPTION = "Analog Devices Fastboot Listener Application"
LICENSE = "CLOSED"

FASTBOOT_GIT_URI ?= "git://github.com/analogdevicesinc/lnxdsp-examples.git"
FASTBOOT_GIT_PROTOCOL = "https"
FASTBOOT_BRANCH ?= "develop/yocto-1.0.0-fastboot"

SRC_URI += " \
	${FASTBOOT_GIT_URI};protocol=${FASTBOOT_GIT_PROTOCOL};branch=${FASTBOOT_BRANCH}"

PR = "r0"

S = "${WORKDIR}/git/fastboot"

SRCREV = "${AUTOREV}"

do_compile(){
	cd ${S}
	${CC} ${CFLAGS} ${LDFLAGS} fastboot_server.c -o fastboot-listener
	gcc ${CFLAGS} fastboot_client.c -o fastboot_client
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/fastboot-listener ${D}/usr/bin/
}
