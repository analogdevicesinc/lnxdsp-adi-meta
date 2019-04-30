SUMMARY = "U-Boot For ADSP-SC5xx Products"

inherit analog-devices-baremetal autotools

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

UBOOT_GIT_URI = "git://git@src.timesys.com/services/analog-devices/analog-devices-yocto-bsp-porting/uboot.git"
UBOOT_GIT_PROTOCOL = "ssh"

SRC_URI += " \
	${UBOOT_GIT_URI};protocol=${UBOOT_GIT_PROTOCOL};branch=${BRANCH} \
        file://0001-drop-configh-from-tools.patch \
        file://uboot-tools-nommu.patch \
"

FILES_${PN} = " \
	u-boot.ldr \
"

PV_append = "+git${SRCPV}"

DEPENDS += "dtc-native bc-native lzop-native"

UBOOT_SUFFIX ?= "img"

PROVIDES += "u-boot"
PKG_${PN} = "u-boot"
PKG_${PN}-dev = "u-boot-dev"
PKG_${PN}-dbg = "u-boot-dbg"

S = "${WORKDIR}/git"

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
}

do_deploy() {
	install ${S}/u-boot.ldr ${DEPLOYDIR}/u-boot.ldr
}
