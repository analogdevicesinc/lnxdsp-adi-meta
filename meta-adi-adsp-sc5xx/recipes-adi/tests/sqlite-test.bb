DESCRIPTION = "Analog Devices SQLite Test Utility"
LICENSE = "CLOSED"

SRC_URI = " \
	svn://svn.code.sf.net/p/adi-openapp/code/trunk/tests;module=sqlite_test;protocol=http;rev=HEAD \
"

DEPENDS = "sqlite3"

S = "${WORKDIR}/sqlite_test"

do_compile_prepend(){
	cd ${S}
}

do_install(){
	install -d ${D}/usr/
	install -d ${D}/usr/bin
	install -m 755 ${S}/sqlite_test ${D}/usr/bin/
	install -m 755 ${S}/wishlist.sql ${D}/usr/
}

FILES_${PN} += " \
	/usr/wishlist.sql \
"