SUMMARY = "Analog Devices INITRAMFS script"
LICENSE = "CLOSED"

SRC_URI = "file://init-ramfs.sh"

RDEPENDS:${PN} += "nfs-utils-mount"

S = "${WORKDIR}"

do_install() {
	install -m 0755 ${WORKDIR}/init-ramfs.sh ${D}/init
}

inherit allarch

FILES:${PN} += "/init"
