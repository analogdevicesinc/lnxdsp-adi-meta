DESCRIPTION = "Analog Devices Video Test Utility"
LICENSE = "CLOSED"

SRC_URI = " \
	svn://svn.code.sf.net/p/adi-openapp/code/trunk/tests;module=video_test;protocol=http;rev=HEAD \
"

S = "${WORKDIR}/video_test"

do_compile_prepend(){
	cd ${S}
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/video_test ${D}/usr/bin/
}