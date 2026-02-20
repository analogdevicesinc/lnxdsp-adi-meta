LICENSE="CLOSED"

INSANE_SKIP:${PN} += "ldflags"

SRC_URI += " \
	file://source/adi-crc.c \
	file://source/adi-crc.sh \
"

S="${WORKDIR}/source"

DEPENDS += "cryptodev-linux openssl"

RDEPENDS:${PN} += "cryptodev-module"

do_compile(){
	${CC} -o adi-crc adi-crc.c -lssl -lcrypto
}

do_install(){
	install -d ${D}/crypto
	install -m 0777 ${S}/adi-crc ${D}/crypto/adi-crc
	install -m 0777 ${S}/adi-crc.sh ${D}/crypto/adi-crc.sh
}

FILES:${PN} += " \
	/crypto/adi-crc \
	/crypto/adi-crc.sh \
"
