
require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

COMPATIBLE_MACHINE = "adsp-sc598-som-ezkit"

TFA_GIT_URI ?= "git://github.com/analogdevicesinc/trusted-firmware-a.git"
TFA_GIT_PROTOCOL ?= "https"
TFA_GIT_BRANCH ?= "tfa-2.14.8"

SRC_URI = "${TFA_GIT_URI};protocol=${TFA_GIT_PROTOCOL};name=tfa;branch=${TFA_GIT_BRANCH}"

SRCREV_FORMAT = "tfa"

SRCREV_tfa = "558a55cb7a344086c9d88102813a7e7f7b56277b"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=6ed7bace7b0bc63021c6eba7b524039e"

TFA_PLATFORM = "adsp_sc598"
TFA_BUILD_TARGET = "bl31"
TFA_SPD = "opteed"

do_deploy:append() {
    install -m 0644 ${D}${FIRMWARE_DIR}/bl31.elf ${DEPLOYDIR}/
    install -m 0644 ${D}${FIRMWARE_DIR}/bl31.bin ${DEPLOYDIR}/
}
