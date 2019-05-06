require u-boot-adi.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=c7383a594871c03da76b3707929d2919"

PR = "r0"

BRANCH = "release/linuxaddin-1.3.0"

SRCREV = "198a2cc8231158b2f61849d74364b19228a44854"

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
