SUMMARY = "Analog Devices INITRAMFS script"
LICENSE = "CLOSED"

SRC_URI = "file://init-ramfs.sh"

S = "${WORKDIR}"

do_install() {
	install -m 0755 ${WORKDIR}/init-ramfs.sh ${D}/init
}

inherit allarch

FILES_${PN} += "/init"
