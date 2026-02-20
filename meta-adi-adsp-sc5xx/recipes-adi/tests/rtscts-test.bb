DESCRIPTION = "Analog Devices UART RTSCTS Test Utility"
LICENSE = "CLOSED"

SRC_URI = " \
	svn://svn.code.sf.net/p/adi-openapp/code/trunk/tests;module=rtscts_test;protocol=http;rev=HEAD \
"

S = "${WORKDIR}/rtscts_test"

do_compile:prepend(){
	cd ${S}
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/rtscts_test ${D}/usr/bin/
}
