DESCRIPTION = "Analog Devices Pthread Test Utility"
LICENSE = "CLOSED"

SRC_URI = " \
       svn://svn.code.sf.net/p/adi-openapp/code/trunk/tests;module=pthread_test;protocol=http;rev=HEAD \
"

S = "${WORKDIR}/pthread_test"

do_compile_prepend(){
	cd ${S}
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/ex? ${D}/usr/bin/
	install -m 755 ${S}/ptest ${D}/usr/bin/
}
