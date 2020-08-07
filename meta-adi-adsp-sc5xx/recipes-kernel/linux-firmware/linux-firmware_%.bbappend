SUMMARY = "Linux kernel firmware files from ADI distribution"
DESCRIPTION = "These binaries provide kernel support for ADI sc5xx boards"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-firmware:"
SRC_URI += " \
			file://sharc_core0.ldr \
			file://LICENSE.adi \
			"
SRC_URI[LICENSE.adi.md5sum] = "e2bfd7246b6d241634f71dfdbfef3d41"

do_install_append() {
	install -m 0644 ${WORKDIR}/sharc_core0.ldr ${D}/lib/firmware/
}

PACKAGES =+ " \
			${PN}-fastboot \
			"
FILES_${PN}-fastboot = " \
		/lib/firmware/sharc_core0.ldr \
		"