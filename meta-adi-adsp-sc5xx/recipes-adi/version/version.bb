DESCRIPTION = "Analog Devices Version Utility"
LICENSE = "CLOSED"

SRC_URI = " \
	svn://svn.code.sf.net/p/adi-openapp/code/trunk/apps;module=version;protocol=http;rev=HEAD \
"

S = "${WORKDIR}/version"

do_compile_prepend(){
	cd ${S}
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/version ${D}/usr/bin/
}