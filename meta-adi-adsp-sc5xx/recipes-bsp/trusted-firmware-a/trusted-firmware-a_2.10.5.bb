
require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

COMPATIBLE_MACHINE = "adsp-sc598-som-ezkit"

TFA_GIT_URI ?= "git://github.com/analogdevicesinc/trusted-firmware-a.git"
TFA_GIT_PROTOCOL ?= "https"
TFA_GIT_BRANCH ?= "develop/3.1.1"

SRC_URI = "${TFA_GIT_URI};protocol=${TFA_GIT_PROTOCOL};name=tfa;branch=${TFA_GIT_BRANCH}"

SRCREV_FORMAT = "tfa"

SRCREV_tfa = "c5c2fb000fb6b6ae34ed109aba04f81a89385066"

LIC_FILES_CHKSUM += "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

S = "${WORKDIR}/git"

TFA_PLATFORM = "adsp_sc598"
TFA_BUILD_TARGET = "bl31"
TFA_SPD = "opteed"
