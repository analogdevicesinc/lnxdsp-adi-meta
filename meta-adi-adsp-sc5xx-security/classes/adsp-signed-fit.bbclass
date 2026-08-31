do_assemble_fitimage:append:adsp-sc5xx-signedboot() {
	uboot-mkimage -D "${UBOOT_MKIMAGE_DTCOPTS}" -F -k ${UBOOT_SIGN_KEYDIR} \
		-r fitImage
}
