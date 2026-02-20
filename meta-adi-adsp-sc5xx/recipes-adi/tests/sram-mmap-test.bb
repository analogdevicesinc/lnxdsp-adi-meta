DESCRIPTION = "Analog Devices SRAM MMAP Test Utility"
LICENSE = "CLOSED"

SRC_URI = " \
       svn://svn.code.sf.net/p/adi-openapp/code/trunk/tests;module=sram_mmap_test;protocol=http;rev=HEAD \
"

S = "${WORKDIR}/sram_mmap_test"

do_compile:prepend(){
	cd ${S}
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/sram_mmap ${D}/usr/bin/
}
