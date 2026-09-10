SUMMARY = "Analog Devices INITRAMFS script"
LICENSE = "CLOSED"

SRC_URI = "file://init-ramfs.sh"

RDEPENDS:${PN} += "nfs-utils-mount"

S = "${UNPACKDIR}"

do_install() {
	install -m 0755 ${UNPACKDIR}/init-ramfs.sh ${D}/init
}

inherit allarch

FILES:${PN} += "/init"
