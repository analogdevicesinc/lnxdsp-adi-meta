SECTION = "kernel"
DESCRIPTION = "Linux kernel for Analog Devices ADSP-5xx processors"

inherit adsp-sc5xx-compatible

require recipes-kernel/linux/linux-yocto.inc

KERNEL_GIT_URI ?= "git://github.com/analogdevicesinc/linux.git"
KERNEL_GIT_PROTOCOL ?= "https"

LNX_SOM_FILE = ""
LNX_SOM_FILE:adsp-sc598-som-ezkit = "file://0001-sc598-som-Adding-support-for-revision-D.patch"
LNX_SOM = "${@bb.utils.contains('SOM_REV', 'D', '${LNX_SOM_FILE}', '', d)}"

SRC_URI += " \
	${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${KERNEL_BRANCH} \
        ${LNX_SOM} \
"

# Pull in the devicetree files into the rootfs
# RDEPENDS:kernel-base += "kernel-devicetree"

KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

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
KBUILD_DEFCONFIG:adsp-sc846-som-ezkit = "sc846-som-ezkit_defconfig"

KBUILD_DEFCONFIG ?= "defconfig"

KERNEL_IMAGETYPE_FOR_MAKE = "${KERNEL_IMAGETYPE}"
