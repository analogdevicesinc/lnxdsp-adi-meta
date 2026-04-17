SECTION = "kernel"
DESCRIPTION = "Linux kernel for Analog Devices ADSP-5xx processors"

inherit adsp-sc5xx-compatible

require recipes-kernel/linux/linux-yocto.inc

KERNEL_GIT_URI ?= "git://github.com/analogdevicesinc/linux.git"
KERNEL_GIT_PROTOCOL ?= "https"

LNX_SOM_FILE = ""
LNX_SOM_FILE:adsp-sc598-som-ezkit = "file://0001-sc598-som-Adding-support-for-revision-D.patch"
LNX_SOM = "${@bb.utils.contains('SOM_REV', 'D', '${LNX_SOM_FILE}', '', d)}"

python() {
    d.setVar('SDCARD_PATCH', '')
    
    if d.getVar('SOM_REV') != 'D':
        d.setVar('SDCARD_PATCH', ' file://0001-sc598-som-enable-SD-card-revE.patch')
    else:
        d.setVar('SDCARD_PATCH', ' file://0001-sc598-som-enable-SD-card.patch')
}

LNX_BRD_FILE = ""
LNX_BRD_FILE:adsp-sc589-mini = "file://0001-mclk-rev2.1-sc5xx-asoc-card.patch"
LNX_BRD = "${@bb.utils.contains('BRD_REV', '2.1', '${LNX_BRD_FILE}', '', d)}"

SRC_URI += " \
	${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${KERNEL_BRANCH} \
        ${LNX_SOM} \
        ${LNX_BRD} \
"

# Pull in the devicetree files into the rootfs
# RDEPENDS:kernel-base += "kernel-devicetree"

KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

S = "${WORKDIR}/git"

# Append to the MACHINE_KERNEL_PR so that a new SRCREV will cause a rebuild
MACHINE_KERNEL_PR:append = "a"
PR = "${MACHINE_KERNEL_PR}"

# Use DEFCONFIGs for configuring linux-adi kernels
KCONFIG_MODE ?= "alldefconfig"

KBUILD_DEFCONFIG:adsp-sc589-ezkit = "sc589-ezkit_defconfig"
KBUILD_DEFCONFIG:adsp-sc584-ezkit = "sc584-ezkit_defconfig"
KBUILD_DEFCONFIG:adsp-sc573-ezlite = "sc573-ezlite_defconfig"
KBUILD_DEFCONFIG:adsp-sc589-mini = "sc589-mini_defconfig"
KBUILD_DEFCONFIG:adsp-sc594-som-ezkit = "sc594-som-ezkit_defconfig"
KBUILD_DEFCONFIG:adsp-sc598-som-ezkit = "sc598-som-ezkit_defconfig"
KBUILD_DEFCONFIG:adsp-sc594-som-ezlite = "sc594-som-ezlite_defconfig"
KBUILD_DEFCONFIG:adsp-sc598-som-ezlite = "sc598-som-ezlite_defconfig"

KERNEL_IMAGETYPE_FOR_MAKE = "${KERNEL_IMAGETYPE}"
