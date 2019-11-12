FILESEXTRAPATHS_prepend := "${THISDIR}/alsa-utils:"

SRC_URI += " \
	file://0001-fix-invalid-file-size-check-for-non-regular-files.patch \
"
