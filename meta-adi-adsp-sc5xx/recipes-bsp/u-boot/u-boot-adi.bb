require u-boot-adi.inc

PR = "r0"

SRCREV = "${AUTOREV}"

UBOOT_INITIAL_ENV = ""

EMMC_BOOT_STAGES = ""
EMMC_BOOT_STAGES_adsp-sc598-som-ezkit = "u-boot-${BOARD}.ldr.emmc_boot_stage1 u-boot-${BOARD}.ldr.emmc_boot_stage2"

FILES-SPL = " \
    u-boot-proper-${BOARD}.elf \
    u-boot-spl-${BOARD}.elf \
    u-boot-proper-${BOARD}.ldr \
    u-boot-spl-${BOARD}.ldr \
    u-boot-uart-${BOARD}.ldr \
"

FILES-NO-SPL = " \
    u-boot-${BOARD}.ldr \
    ${EMMC_BOOT_STAGES} \
    u-boot-${BOARD} \
    init-${BOARD}.elf \
"

FILES_${PN} = "${@bb.utils.contains_any('MACHINE_FEATURES', 'spl falcon', '${FILES-SPL}', '${FILES-NO-SPL}', d)}"

INIT_PATH = ""
INIT_PATH_adsp-sc573-ezkit = "arch/arm/cpu/armv7/sc57x"
INIT_PATH_adsp-sc584-ezkit = "arch/arm/cpu/armv7/sc58x"
INIT_PATH_adsp-sc589-ezkit = "arch/arm/cpu/armv7/sc58x"
INIT_PATH_adsp-sc589-mini = "arch/arm/cpu/armv7/sc58x"
INIT_PATH_adsp-sc594-som-ezkit = "arch/arm/cpu/armv7/sc59x"
INIT_PATH_adsp-sc598-som-ezkit = "arch/arm/cpu/armv8/sc59x-64"

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
        install ${B}/u-boot-proper-${BOARD}.elf ${D}/
        install ${B}/u-boot-spl-${BOARD}.elf ${D}/
        install ${B}/u-boot-proper-${BOARD}.ldr ${D}/
        install ${B}/u-boot-spl-${BOARD}.ldr ${D}/
        install ${B}/u-boot-uart-${BOARD}.ldr ${D}/
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
        install ${B}/u-boot-proper-${BOARD}.elf ${DEPLOYDIR}/
        install ${B}/u-boot-spl-${BOARD}.elf ${DEPLOYDIR}/
        install ${B}/u-boot-proper-${BOARD}.ldr ${DEPLOYDIR}/
        install ${B}/u-boot-spl-${BOARD}.ldr ${DEPLOYDIR}/
        install ${B}/u-boot-uart-${BOARD}.ldr ${DEPLOYDIR}/
    fi
}
