DESCRIPTION = "Analog Devices Play Utility"
LICENSE = "CLOSED"

SRC_URI = " \
	svn://svn.code.sf.net/p/adi-openapp/code/trunk/apps;module=play;protocol=http;rev=HEAD \
"

S = "${WORKDIR}/play"

do_compile:prepend(){
	cd ${S}
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/play ${D}/usr/bin/
	install -m 755 ${S}/tone ${D}/usr/bin/	
}