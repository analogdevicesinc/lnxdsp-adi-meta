DESCRIPTION = "Analog Devices Core Control Utility"
LICENSE = "CLOSED"

SRC_URI = " \
	file://source/corecontrol.c \
"

S = "${WORKDIR}/source"

DEPENDS = "kernel-devsrc virtual/kernel"

TARGET_CFLAGS += "-D __ADSPSC5xx__ -I${STAGING_KERNEL_DIR}/drivers/staging/icc/include"

do_compile(){
	cd ${S}
	${CC} ${CFLAGS} ${LDFLAGS} corecontrol.c -o corecontrol
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/corecontrol ${D}/usr/bin/
}