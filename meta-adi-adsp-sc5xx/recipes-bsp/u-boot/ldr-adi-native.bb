DESCRIPTION = "Analog Devices Loader Utility"
LICENSE = "CLOSED"

inherit autotools pkgconfig gettext native

LDR_GIT_URI ?= "git://github.com/analogdevicesinc/lnxdsp-arm-poky-linux-gnueabi-ldr.git"
LDR_GIT_PROTOCOL ?= "https"
LDR_GIT_BRANCH ?= "release/2.1.0"

PR = "r1"
SRCREV= "a193b4fcdc541803d32573470a802b5ade9a3913"

SRC_URI = " \
${LDR_GIT_URI};protocol=${LDR_GIT_PROTOCOL};branch=${LDR_GIT_BRANCH}"

S = "${WORKDIR}/git/src/ldr"

do_install(){
    install -d ${D}${bindir}/
    install -m 755 ldr ${D}${bindir}/arm-poky-linux-gnueabi-ldr
    install -m 755 ldr ${D}${bindir}/aarch64-poky-linux-ldr
}
