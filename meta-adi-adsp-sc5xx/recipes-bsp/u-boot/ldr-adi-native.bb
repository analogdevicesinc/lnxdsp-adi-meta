DESCRIPTION = "Analog Devices Loader Utility"
LICENSE = "CLOSED"

inherit autotools pkgconfig gettext native

LDR_GIT_URI ?= "git://git@src.timesys.com/services/analog-devices/analog-devices-new-board-bringup-1/arm-poky-linux-gnueabi-ldr.git"
LDR_GIT_PROTOCOL ?= "ssh"
LDR_GIT_BRANCH ?= "master"

PR = "r0"
SRCREV= "${AUTOREV}"

SRC_URI = " \
${LDR_GIT_URI};protocol=${LDR_GIT_PROTOCOL};branch=${LDR_GIT_BRANCH}"

S = "${WORKDIR}/git/src/ldr"

do_install(){
    install -d ${D}${bindir}/
    install -m 755 ldr ${D}${bindir}/arm-poky-linux-gnueabi-ldr
    install -m 755 ldr ${D}${bindir}/aarch64-poky-linux-ldr
}
