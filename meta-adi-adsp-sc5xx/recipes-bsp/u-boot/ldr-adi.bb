DESCRIPTION = "Analog Devices Loader Utility"
LICENSE = "CLOSED"

inherit autotools pkgconfig gettext meson

LDR_GIT_URI ?= "git://github.com/analogdevicesinc/adsp-ldr.git"
LDR_GIT_PROTOCOL ?= "https"
LDR_GIT_BRANCH ?= "philip/sc846"

SRCREV= "${AUTOREV}"

SRC_URI = " \
${LDR_GIT_URI};protocol=${LDR_GIT_PROTOCOL};branch=${LDR_GIT_BRANCH}"

S = "${WORKDIR}/git"

BBCLASSEXTEND += "native nativesdk"

DEPENDS += " ninja libusb1 "
