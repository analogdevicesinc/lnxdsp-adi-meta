SECTION = "kernel"
DESCRIPTION = "Linux kernel for Analog Devices ADSP-5xx processors"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=..."

inherit kernel

# Look in the generic major.minor directory for files
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-4.0:"

# Pull in the devicetree files into the rootfs
RDEPENDS_kernel-base += "kernel-devicetree"

KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

S = "${WORKDIR}/git"

BRANCH = "master"

SRCREV = "..."
PV = "4.0+git${SRCPV}"

# Append to the MACHINE_KERNEL_PR so that a new SRCREV will cause a rebuild
MACHINE_KERNEL_PR_append = "a"
PR = "${MACHINE_KERNEL_PR}"

KERNEL_GIT_URI = "git://.../linux-adi.git"
KERNEL_GIT_PROTOCOL = "git"

KERNEL_DEVICETREE_adsp-sc573-ezkit += "....dtb"
KERNEL_DEVICETREE_adsp-sc584-ezkit += "....dtb"
KERNEL_DEVICETREE_adsp-sc589-ezkit += "....dtb"
KERNEL_DEVICETREE_adsp-sc589-mini  += "....dtb"

SRC_URI += " \
	${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${BRANCH} \
"

do_configure_append(){
	cp ${WORKDIR}/some_defconfig ${WORKDIR}/git/arch/arm/configs
	make some_defconfig
}