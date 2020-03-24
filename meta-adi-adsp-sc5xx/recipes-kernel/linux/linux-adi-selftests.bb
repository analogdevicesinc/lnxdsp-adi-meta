DESCRIPTION = "Kernel Self Tests"
LICENSE = "CLOSED"

require linux-adi-src.inc

SRCBRANCH = "develop/yocto-1.0.0"
SRCREV = "${AUTOREV}"

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
