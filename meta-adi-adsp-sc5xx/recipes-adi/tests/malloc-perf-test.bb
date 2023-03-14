DESCRIPTION = "Analog Devices Malloc Perf Test Utility"
LICENSE = "CLOSED"

SRC_URI = " \
	svn://svn.code.sf.net/p/adi-openapp/code/trunk/tests;module=malloc-perf;protocol=http;rev=HEAD \
"

S = "${WORKDIR}/malloc-perf"

do_compile:prepend(){
	cd ${S}
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/malloc-perf ${D}/usr/bin/
}
