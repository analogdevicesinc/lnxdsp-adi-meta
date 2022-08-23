require u-boot-adi.inc

PR = "r0"

SRCREV = "${AUTOREV}"

UBOOT_INITIAL_ENV = ""

EMMC_BOOT_STAGES = ""
EMMC_BOOT_STAGES_adsp-sc598-som-ezkit = "u-boot-${BOARD}.ldr.emmc_boot_stage1 u-boot-${BOARD}.ldr.emmc_boot_stage2"

STAGE_1_TARGET_NAME = "stage1-boot.ldr"

FILES-SPL = " \
    u-boot-proper-${BOARD}.elf \
    u-boot-spl-${BOARD}.elf \
    ${STAGE_1_TARGET_NAME} \
"

FILES-SPL_adsp-sc594-som-ezkit = " \
    u-boot-proper-${BOARD}.img \
    u-boot-proper-${BOARD}.elf \
    u-boot-spl-${BOARD}.elf \
    ${STAGE_1_TARGET_NAME} \
"

FILES-NO-SPL = " \
    u-boot-${BOARD}.ldr \
    ${EMMC_BOOT_STAGES} \
    u-boot-${BOARD} \
    init-${BOARD}.elf \
"

FILES_${PN} = "${@bb.utils.contains_any('MACHINE_FEATURES', 'spl falcon', '${FILES-SPL}', '${FILES-NO-SPL}', d)}"

INIT_PATH = "board/adi/common/init"

COMPATIBLE_MACHINE = "(adsp-sc573-ezkit|adsp-sc584-ezkit|adsp-sc589-ezkit|adsp-sc589-mini|adsp-sc594-som-ezkit|adsp-sc598-som-ezkit)"

do_configure_prepend() {
	if [ -n "${BOARD_HAS_SPL}" ]; then
		bbwarn "You should consider using SPL or falcon boot instead of legacy boot"
	fi
}

do_install () {
    if [ -z "${BASH_HAS_SPL}" ]; then
        install ${B}/u-boot-${BOARD}.ldr ${D}/
        if [ "${MACHINE}" = "adsp-sc598-som-ezkit" ]; then
            install ${B}/u-boot-${BOARD}.ldr.emmc_boot_stage1 ${D}/
            install ${B}/u-boot-${BOARD}.ldr.emmc_boot_stage2 ${D}/
        fi
        install ${B}/u-boot-${BOARD} ${D}/
        install ${B}/${INIT_PATH}/init-${BOARD}.elf ${D}/
    else
        if [ "${MACHINE}" = "adsp-sc594-som-ezkit" ]; then
            install ${B}/u-boot-proper-${BOARD}.img ${D}/
        fi
        install ${B}/u-boot-proper-${BOARD}.elf ${D}/
        install ${B}/u-boot-spl-${BOARD}.elf ${D}/
        install ${B}/u-boot-spl-${BOARD}.ldr ${D}/${STAGE_1_TARGET_NAME}
    fi
}

do_deploy() {
    if [ -z "${BASH_HAS_SPL}" ]; then
        install ${B}/u-boot-${BOARD}.ldr ${DEPLOYDIR}/
        if [ "${MACHINE}" = "adsp-sc598-som-ezkit" ]; then
            install ${B}/u-boot-${BOARD}.ldr.emmc_boot_stage1 ${DEPLOYDIR}/
            install ${B}/u-boot-${BOARD}.ldr.emmc_boot_stage2 ${DEPLOYDIR}/
        fi
        install ${B}/u-boot-${BOARD} ${DEPLOYDIR}/
        install ${B}/${INIT_PATH}/init-${BOARD}.elf ${DEPLOYDIR}/
    else
        if [ "${MACHINE}" = "adsp-sc594-som-ezkit" ]; then
            install ${B}/u-boot-proper-${BOARD}.img ${DEPLOYDIR}/
        fi
        install ${B}/u-boot-proper-${BOARD}.elf ${DEPLOYDIR}/
        install ${B}/u-boot-spl-${BOARD}.elf ${DEPLOYDIR}/
        install ${B}/u-boot-spl-${BOARD}.ldr ${DEPLOYDIR}/${STAGE_1_TARGET_NAME}
    fi
}
