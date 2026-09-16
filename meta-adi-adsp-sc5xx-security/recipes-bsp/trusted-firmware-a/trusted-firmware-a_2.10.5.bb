
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

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# OS boot: BL31 reads the kernel FIT straight from SPI flash (the same offset the
# non-secure boot uses) and enters the kernel as BL33. No U-Boot proper.
ADI_KERNEL_FIT_SPI_OFFSET ?= "0x100000"
SRC_URI += "${@' file://0001-adsp_sc598-boot-kernel-fit-from-spi.patch' if bb.utils.to_boolean(d.getVar('ADI_OS_BOOT')) else ''}"
EXTRA_OEMAKE += "${@' ADI_KERNEL_FIT_SPI_OFFSET=${ADI_KERNEL_FIT_SPI_OFFSET}' if bb.utils.to_boolean(d.getVar('ADI_OS_BOOT')) else ''}"
