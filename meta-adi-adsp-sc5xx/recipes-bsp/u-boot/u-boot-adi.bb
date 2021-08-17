require u-boot-adi.inc

PR = "r0"

SRCREV = "${AUTOREV}"

UBOOT_INITIAL_ENV = ""

SRC_URI += " \
    file://arm-poky-linux-gnueabi-ldr \
    file://elfloader.tar.gz \
"

FILES_${PN} = " \
    u-boot-${BOARD}.ldr \
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

do_elfloader_agree_sla(){
	if [ "${ADI_ELFLOADER_AGREE_SLA}" != "1" ]; then
		bbfatal "You must agree to the Analog Devices Incorporated Software License Agreement (elfloader-sla.pdf) to use elfloader.  Please set ADI_ELFLOADER_AGREE_SLA=\"1\" in your local.conf if you agree"
	fi
}

addtask elfloader_agree_sla before do_unpack

#do_unpack_elfloader(){
#	cd ${WORKDIR}
#	tar -xf 
#}

do_compile_prepend(){
    #Use U-boot's FDT header files, not Linux's (in case they are different)
    #cp ${LIBFDT_ENV_H_FILE} ${WORKDIR}/recipe-sysroot-native/usr/include/libfdt_env.h
    #cp ${LIBFDT_H_FILE} ${WORKDIR}/recipe-sysroot-native/usr/include/libfdt.h

    #Add arm-poky-linux-gnueabi-ldr/elfloader in to path
    export PATH=$PATH:${WORKDIR}
}

do_install () {
    install ${B}/u-boot-${BOARD}.ldr ${D}/
    install ${B}/u-boot-${BOARD} ${D}/
    install ${B}/${INIT_PATH}/init-${BOARD}.elf ${D}/
}

do_deploy() {
    install ${B}/u-boot-${BOARD}.ldr ${DEPLOYDIR}/
    install ${B}/u-boot-${BOARD} ${DEPLOYDIR}/
    install ${B}/${INIT_PATH}/init-${BOARD}.elf ${DEPLOYDIR}/
}
