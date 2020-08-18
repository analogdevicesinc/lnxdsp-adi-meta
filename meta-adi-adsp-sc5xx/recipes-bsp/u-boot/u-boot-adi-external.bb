require u-boot-adi.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=c7383a594871c03da76b3707929d2919"

PR = "r0"

UBOOT_BRANCH ?= "release/yocto-1.0.0"

SRCREV = "${AUTOREV}"

FILES_${PN} = " \
	u-boot-spl.ldr \
	u-boot-${BOARD} \
	u-boot-${BOARD}.bin \
	init-${BOARD}.elf \
"
INIT_PATH = "${@ 'arch/arm/cpu/armv7/%s' %('sc57x' if MACHINE == 'adsp-sc573-ezkit' else 'sc58x')}"

do_configure () {
	setup_analog_devices_baremetal
	cd ${S}
	make distclean
	make ${UBOOT_MACHINE}
}

do_compile () {
	setup_analog_devices_baremetal
	cd ${S}
	make
}

do_install () {
	install ${S}/u-boot-spl.ldr ${D}/
	install ${S}/u-boot-${BOARD} ${D}/
	install ${S}/u-boot-${BOARD}.bin ${D}/
	install ${S}/${INIT_PATH}/init-${BOARD}.elf ${D}/
}

do_deploy() {
	install ${S}/u-boot-spl.ldr ${DEPLOYDIR}/
	install ${S}/u-boot-${BOARD} ${DEPLOYDIR}/
	install ${S}/u-boot-${BOARD}.bin ${DEPLOYDIR}/
	install ${S}/${INIT_PATH}/init-${BOARD}.elf ${DEPLOYDIR}/
}
