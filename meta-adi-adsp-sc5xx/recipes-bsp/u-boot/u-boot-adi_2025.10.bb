inherit adsp-sc5xx-compatible

require u-boot-adi.inc

SRCREV = "1d6062f1f6a5d7ca926ad2c31bfac858f5be2a16"

UBOOT_INITIAL_ENV = ""

STAGE_1_TARGET_NAME = "u-boot-spl.ldr"

FILES:${PN} = " \
    u-boot-proper-${BOARD}.elf \
    u-boot-spl-${BOARD}.elf \
    ${STAGE_1_TARGET_NAME} \
"

EXTRA_OEMAKE += "LDR=ldr"

do_install () {
	install ${B}/u-boot ${D}/u-boot-proper-${BOARD}.elf
	install ${B}/spl/u-boot-spl ${D}/u-boot-spl-${BOARD}.elf
	install ${B}/spl/u-boot-spl.ldr ${D}/${STAGE_1_TARGET_NAME}
}

do_deploy() {
	install ${B}/u-boot ${DEPLOYDIR}/u-boot-proper-${BOARD}.elf
	install ${B}/spl/u-boot-spl ${DEPLOYDIR}/u-boot-spl-${BOARD}.elf
	install ${B}/spl/u-boot-spl.ldr ${DEPLOYDIR}/${STAGE_1_TARGET_NAME}
}

INSANE_SKIP:${PN} += "textrel"
