require u-boot-adi.inc

PR = "r0"

SRCREV = "${AUTOREV}"

FILES_${PN} = " \
	u-boot.ldr \
	u-boot \
"

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
	install ${S}/u-boot.ldr ${D}/u-boot.ldr
	install ${S}/u-boot ${D}/u-boot
}

do_deploy() {
	install ${S}/u-boot.ldr ${DEPLOYDIR}/u-boot.ldr
	install ${S}/u-boot ${DEPLOYDIR}/u-boot
}
