require u-boot-adi.inc

PR = "r0"

SRCREV = "${AUTOREV}"

UBOOT_INITIAL_ENV = ""

def emmc_boot_stages(d):
  MACHINE = d.getVar('MACHINE')
  if MACHINE == 'adsp-sc598-som-ezkit':
    return "u-boot-${BOARD}.ldr.emmc_boot_stage1 u-boot-${BOARD}.ldr.emmc_boot_stage2"
  return ""

FILES_${PN} = " \
    u-boot-${BOARD}.ldr \
    ${@emmc_boot_stages(d)} \
    u-boot-${BOARD} \
    init-${BOARD}.elf \
"

python () {
    MACHINE = d.getVar('MACHINE')
    if MACHINE == 'adsp-sc573-ezkit':
        PATH = "sc57x"
        ARCH = "armv7"
    elif MACHINE == 'adsp-sc584-ezkit':
        PATH = "sc58x"
        ARCH = "armv7"
    elif MACHINE == 'adsp-sc589-ezkit':
        PATH = "sc58x"
        ARCH = "armv7"
    elif MACHINE == 'adsp-sc589-mini':
        PATH = "sc58x"
        ARCH = "armv7"
    elif MACHINE == 'adsp-sc594-som-ezkit':
        PATH = "sc59x"
        ARCH = "armv7"
    elif MACHINE == 'adsp-sc598-som-ezkit':
        PATH = "sc59x-64"
        ARCH = "armv8"

    d.setVar('INIT_PATH', "arch/arm/cpu/" + ARCH + "/" + PATH)

    d.setVar('LIBFDT_ENV_H_FILE', "${WORKDIR}/git/include/linux/libfdt_env.h")
    d.setVar('LIBFDT_H_FILE', "${WORKDIR}/git/include/linux/libfdt.h")
}

do_compile_prepend(){
    #Use U-boot's FDT header files, not Linux's (in case they are different)
    #cp ${LIBFDT_ENV_H_FILE} ${WORKDIR}/recipe-sysroot-native/usr/include/libfdt_env.h
    #cp ${LIBFDT_H_FILE} ${WORKDIR}/recipe-sysroot-native/usr/include/libfdt.h

    #Add arm-poky-linux-gnueabi-ldr in to path
    #(This is probably unnecessary now -- leaving just in case)
    export PATH=$PATH:${WORKDIR}/recipe-sysroot-native/usr/bin
}

do_install () {
    install ${B}/u-boot-${BOARD}.ldr ${D}/
    if [ "${MACHINE}" = "adsp-sc598-som-ezkit" ]; then
        install ${B}/u-boot-${BOARD}.ldr.emmc_boot_stage1 ${D}/
        install ${B}/u-boot-${BOARD}.ldr.emmc_boot_stage2 ${D}/
    fi
    install ${B}/u-boot-${BOARD} ${D}/
    install ${B}/${INIT_PATH}/init-${BOARD}.elf ${D}/
}

do_deploy() {
    install ${B}/u-boot-${BOARD}.ldr ${DEPLOYDIR}/
    if [ "${MACHINE}" = "adsp-sc598-som-ezkit" ]; then
        install ${B}/u-boot-${BOARD}.ldr.emmc_boot_stage1 ${DEPLOYDIR}/
        install ${B}/u-boot-${BOARD}.ldr.emmc_boot_stage2 ${DEPLOYDIR}/
    fi
    install ${B}/u-boot-${BOARD} ${DEPLOYDIR}/
    install ${B}/${INIT_PATH}/init-${BOARD}.elf ${DEPLOYDIR}/
}
