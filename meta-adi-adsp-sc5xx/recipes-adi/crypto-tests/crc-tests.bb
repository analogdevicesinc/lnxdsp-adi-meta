LICENSE = "CLOSED"

inherit ptest

INSANE_SKIP:${PN} += "ldflags"

SRC_URI += " \
	file://source/adi-crc.c \
	file://source/adi-crc.sh \
	file://run-ptest \
"

S = "${UNPACKDIR}/source"

DEPENDS += "cryptodev-linux openssl"

RDEPENDS:${PN} += "cryptodev-module"

# Substring of the kernel crypto driver name the tests must be served by
ADI_HW_DRIVER ?= "adi"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_compile(){
	${CC} ${@'-DADI_HW_DRIVER=\'"%s"\'' % d.getVar('ADI_HW_DRIVER') if d.getVar('ADI_HW_DRIVER') else ''} \
		-o adi-crc adi-crc.c -lssl -lcrypto
}

do_install(){
	install -d ${D}/crypto
	install -m 0777 ${S}/adi-crc ${D}/crypto/adi-crc
	install -m 0777 ${S}/adi-crc.sh ${D}/crypto/adi-crc.sh
	sed -i 's/@ADI_HW_DRIVER@/${ADI_HW_DRIVER}/' ${D}/crypto/adi-crc.sh
}

FILES:${PN} += " \
	/crypto/adi-crc \
	/crypto/adi-crc.sh \
"
