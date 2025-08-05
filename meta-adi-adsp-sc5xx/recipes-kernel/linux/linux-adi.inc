SECTION = "kernel"
DESCRIPTION = "Linux kernel for Analog Devices ADSP-5xx processors"

inherit adsp-sc5xx-compatible

require recipes-kernel/linux/linux-yocto.inc

KERNEL_GIT_URI ?= "git://github.com/analogdevicesinc/linux.git"
KERNEL_GIT_PROTOCOL ?= "https"

LNX_CRR_FILE = ""
LNX_CRR_FILE:adsp-sc598-som-ezkit = "file://0002-SC598-EZKIT-Adding-softconfig-changes-for-REV-D.patch"
LNX_CRR_FILE:adsp-sc594-som-ezkit = "file://0003-SC594-EZKIT-Adding-softconfig-changes-for-REV-D.patch"
LNX_CRR = "${@bb.utils.contains('CRR_REV', 'D', '${LNX_CRR_FILE}', '', d)}"

LNX_SOM_FILE = ""
LNX_SOM_FILE:adsp-sc598-som-ezkit = "file://0001-sc598-som-Adding-support-for-revision-E.patch"
LNX_SOM = "${@bb.utils.contains('SOM_REV', 'E', '${LNX_SOM_FILE}', '', d)}"

python() {
    d.setVar('SDCARD_PATCH', '')
    d.setVar('FALCON_PATCH', '')
    
    if d.getVar('SOM_REV') != 'E': 
        d.setVar('SDCARD_PATCH', ' file://0001-sc598-som-enable-SD-card.patch')
        d.setVar('FALCON_PATCH',' file://0001-sc598-som-Disabling-peripherals-for-faster-boot.patch')
    else:
        d.setVar('SDCARD_PATCH', ' file://0001-sc598-som-enable-SD-card-revE.patch')
        d.setVar('FALCON_PATCH',' file://0001-sc598-som-Disabling-peripherals-for-a-faster-falcon-boot_revE.patch')
}

LNX_BRD_FILE = ""
LNX_BRD_FILE:adsp-sc589-mini = "file://0001-mclk-rev2.1-sc5xx-asoc-card.patch"
LNX_BRD = "${@bb.utils.contains('BRD_REV', '2.1', '${LNX_BRD_FILE}', '', d)}"

SRC_URI += " \
	${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${KERNEL_BRANCH} \
        ${LNX_CRR} \
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
KBUILD_DEFCONFIG:adsp-sc573-ezkit = "sc573-ezkit_defconfig"
KBUILD_DEFCONFIG:adsp-sc589-mini = "sc589-mini_defconfig"
KBUILD_DEFCONFIG:adsp-sc594-som-ezkit = "sc594-som-ezkit_defconfig"
KBUILD_DEFCONFIG:adsp-sc598-som-ezkit = "sc598-som-ezkit_defconfig"
KBUILD_DEFCONFIG:adsp-sc594-som-ezlite = "sc594-som-ezlite_defconfig"
KBUILD_DEFCONFIG:adsp-sc598-som-ezlite = "sc598-som-ezlite_defconfig"

KERNEL_IMAGETYPE_FOR_MAKE = "${KERNEL_IMAGETYPE}"
