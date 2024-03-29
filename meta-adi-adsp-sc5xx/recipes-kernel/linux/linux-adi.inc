SECTION = "kernel"
DESCRIPTION = "Linux kernel for Analog Devices ADSP-5xx processors"

inherit adsp-sc5xx-compatible

require recipes-kernel/linux/linux-yocto.inc

KERNEL_GIT_URI ?= "git://github.com/analogdevicesinc/lnxdsp-linux.git"
KERNEL_GIT_PROTOCOL ?= "https"

SRC_URI += " \
	${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${KERNEL_BRANCH} \
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
