SUMMARY = "Analog Devices programming INITRAMFS script"
LICENSE = "CLOSED"

FILESEXTRAPATHS:prepend := "${THISDIR}/initramfs-init:"

SRC_URI = "file://programming-init.sh"

S = "${WORKDIR}"

do_install() {
	install -m 0755 ${WORKDIR}/programming-init.sh ${D}/init
}

inherit allarch

FILES:${PN} += "/init"
