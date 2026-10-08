LICENSE = "CLOSED"

INSANE_SKIP:${PN} += "ldflags"

SRC_URI += " \
	file://source/adi-hash.c \
	file://source/adi-hash.sh \
	file://source/adi-skcipher.sh \
"

S = "${UNPACKDIR}/source"

DEPENDS += "cryptodev-module cryptodev-linux openssl"

# Substring of the kernel crypto driver name the tests must be served by
ADI_HW_DRIVER ?= ""
ADI_HW_DRIVER:adsp-sc598-som-ezkit = "adi"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_compile(){
	${CC} ${@'-DADI_HW_DRIVER=\'"%s"\'' % d.getVar('ADI_HW_DRIVER') if d.getVar('ADI_HW_DRIVER') else ''} \
		-o adi-hash adi-hash.c -lssl -lcrypto
}

do_install(){
	install -d ${D}/crypto
	install -m 0777 ${S}/adi-hash ${D}/crypto/adi-hash
	install -m 0777 ${S}/adi-hash.sh ${D}/crypto/adi-hash.sh
	install -m 0777 ${S}/adi-skcipher.sh ${D}/crypto/adi-skcipher.sh
	sed -i 's/@ADI_HW_DRIVER@/${ADI_HW_DRIVER}/' ${D}/crypto/adi-hash.sh ${D}/crypto/adi-skcipher.sh
}

FILES:${PN} += " \
	/crypto/adi-hash \
	/crypto/adi-hash.sh \
	/crypto/adi-skcipher.sh \
"
