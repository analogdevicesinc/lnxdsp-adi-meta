inherit adsp-sc5xx-compatible

require u-boot-adi.inc

SRCREV = "dd19d915e05516b3b4efd2ae03afab8bd29dea0e"

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
