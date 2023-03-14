DESCRIPTION = "Analog Devices RTC Test Utility"
LICENSE = "CLOSED"

SRC_URI = " \
	svn://svn.code.sf.net/p/adi-openapp/code/trunk/tests;module=rtc-test;protocol=http;rev=HEAD \
"

S = "${WORKDIR}/rtc-test"

do_compile:prepend(){
	cd ${S}
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/rtc_test ${D}/usr/bin/
}