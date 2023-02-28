LICENSE="CLOSED"

INSANE_SKIP_${PN} += "ldflags"

SRC_URI += " \
	file://source/adi-hash.c \
	file://source/adi-hash.sh \
	file://source/adi-skcipher.sh \
"

S="${WORKDIR}/source"

DEPENDS += "cryptodev-module cryptodev-linux openssl"

do_compile(){
	${CC} -o adi-hash adi-hash.c -lssl -lcrypto
}

do_install(){
	install -d ${D}/crypto
	install -m 0777 ${S}/adi-hash ${D}/crypto/adi-hash
	install -m 0777 ${S}/adi-hash.sh ${D}/crypto/adi-hash.sh
	install -m 0777 ${S}/adi-skcipher.sh ${D}/crypto/adi-skcipher.sh
}

FILES_${PN} += " \
	/crypto/adi-hash \
	/crypto/adi-hash.sh \
	/crypto/adi-skcipher.sh \
"