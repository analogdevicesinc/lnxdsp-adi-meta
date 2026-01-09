inherit adsp-sc5xx-compatible

require u-boot-adi.inc

SRCREV = "226121ce8036eb409ec1df43f9393a21d584b7c0"

UBOOT_INITIAL_ENV = ""

STAGE_1_TARGET_NAME = "stage1-boot.ldr"

FILES:${PN} = " \
    u-boot-proper-${BOARD}.elf \
    u-boot-spl-${BOARD}.elf \
    ${STAGE_1_TARGET_NAME} \
"

EXTRA_OEMAKE += "LDR=ldr"

do_install () {
	install ${B}/u-boot-proper-${BOARD}.elf ${D}/
	install ${B}/u-boot-spl-${BOARD}.elf ${D}/
	install ${B}/u-boot-spl-${BOARD}.ldr ${D}/${STAGE_1_TARGET_NAME}
}

do_deploy() {
	install ${B}/u-boot-proper-${BOARD}.elf ${DEPLOYDIR}/
	install ${B}/u-boot-spl-${BOARD}.elf ${DEPLOYDIR}/
	install ${B}/u-boot-spl-${BOARD}.ldr ${DEPLOYDIR}/${STAGE_1_TARGET_NAME}
}

INSANE_SKIP:${PN} += "textrel"
