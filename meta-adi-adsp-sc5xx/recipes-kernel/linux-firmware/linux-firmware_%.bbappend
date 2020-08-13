SUMMARY = "Linux kernel firmware files from ADI distribution"
DESCRIPTION = "These binaries provide kernel support for ADI sc5xx boards"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-firmware:"

SRC_URI += " \
			file://adi_adsp_core1_fw.ldr\
			file://LICENSE.adi \
			"
SRC_URI[LICENSE.adi.md5sum] = "e2bfd7246b6d241634f71dfdbfef3d41"

do_install_append() {
	install -m 0644 ${WORKDIR}/adi_adsp_core1_fw.ldr ${D}/lib/firmware/
}

PACKAGES =+ " \
			${PN}-fastboot \
			"
FILES_${PN}-fastboot = " \
		/lib/firmware/adi_adsp_core1_fw.ldr \
		"
