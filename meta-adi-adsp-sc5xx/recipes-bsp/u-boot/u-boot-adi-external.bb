require u-boot-adi.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=c7383a594871c03da76b3707929d2919"

PR = "r0"

BRANCH = "develop/yocto-1.0.0"

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
