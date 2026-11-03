DESCRIPTION = "Analog Devices SRAM MMAP Test Utility"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://sram_mmap.c;beginline=1;endline=3;md5=1fa3c5361967f4bca9b0a2e539a8d442"

# Sources copied from trunk/tests/sram_mmap_test in the adi-openapp SVN
# repository, https://sourceforge.net/p/adi-openapp/code/HEAD/tree/, and
# changed to check for errors and read back the data written to the SRAM

inherit ptest

SRC_URI = " \
	file://source \
	file://run-ptest \
"

S = "${UNPACKDIR}/source"

do_compile(){
	${CC} ${CFLAGS} ${LDFLAGS} -o sram_mmap sram_mmap.c
}

do_install(){
	install -d ${D}${bindir}
	install -m 755 ${S}/sram_mmap ${D}${bindir}/
}
