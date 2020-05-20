DESCRIPTION = "Kernel Self Tests"
LICENSE = "CLOSED"

KERNEL_GIT_URI ?= "git://github.com/analogdevicesinc/lnxdsp-linux.git"
KERNEL_GIT_PROTOCOL = "https"
KERNEL_BRANCH ?= "develop/yocto-1.0.0"
SRCREV = "${AUTOREV}"

SRC_URI += " \
	${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${KERNEL_BRANCH} \
"

S = "${WORKDIR}/git"

do_compile(){
	#Compile testptp
	cd ${S}/tools/testing/selftests/ptp/
	${CC} -o testptp testptp.c -I../../../../usr/include/ -lrt ${LDFLAGS}

	#Compile ...
}

do_install(){
	#Install testptp
	install -d ${D}/usr/bin
	install -m 755 ${S}/tools/testing/selftests/ptp/testptp ${D}/usr/bin/

	#Install ...
}
