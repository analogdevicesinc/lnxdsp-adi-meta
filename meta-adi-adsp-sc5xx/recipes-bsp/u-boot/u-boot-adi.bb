require u-boot-adi.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=c7383a594871c03da76b3707929d2919"

PR = "r0"

UBOOT_BRANCH ?= "release/yocto-1.0.0"

SRCREV = "${AUTOREV}"

SRC_URI += " \
	file://arm-poky-linux-gnueabi-ldr \
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
	elif MACHINE == 'adsp-sc584-ezkit':
		PATH = "sc58x"
	elif MACHINE == 'adsp-sc589-ezkit':
		PATH = "sc58x"
	elif MACHINE == 'adsp-sc589-mini':
		PATH = "sc58x"
	elif MACHINE == 'adsp-sc594-som-ezkit':
		PATH = "sc59x"
	d.setVar('INIT_PATH', "arch/arm/cpu/armv7/" + PATH)
}

do_compile_prepend(){
	#Use U-boot's FDT header files, not Linux's (in case they are different)
	cp ${WORKDIR}/git/include/libfdt_env.h ${WORKDIR}/recipe-sysroot-native/usr/include/libfdt_env.h
	cp ${WORKDIR}/git/include/libfdt.h ${WORKDIR}/recipe-sysroot-native/usr/include/libfdt.h

	#Add arm-poky-linux-gnueabi-ldr in to path
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
