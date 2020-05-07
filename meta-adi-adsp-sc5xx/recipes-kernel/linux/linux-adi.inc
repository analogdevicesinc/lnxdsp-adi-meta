SECTION = "kernel"
DESCRIPTION = "Linux kernel for Analog Devices ADSP-5xx processors"

require recipes-kernel/linux/linux-yocto.inc

KERNEL_GIT_URI = "https://github.com/analogdevicesinc/lnxdsp-linux.git"
KERNEL_GIT_PROTOCOL = "https"

# Put a local version until we have a true SRCREV to point to
SRCBRANCH ?= ""

SRC_URI += " \
	${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${SRCBRANCH} \
"

# Pull in the devicetree files into the rootfs
# RDEPENDS_kernel-base += "kernel-devicetree"

KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

S = "${WORKDIR}/git"

# Append to the MACHINE_KERNEL_PR so that a new SRCREV will cause a rebuild
MACHINE_KERNEL_PR_append = "a"
PR = "${MACHINE_KERNEL_PR}"

# Use DEFCONFIGs for configuring linux-adi kernels
KCONFIG_MODE ?= "alldefconfig"

KBUILD_DEFCONFIG_adsp-sc589-ezkit = "sc589-ezkit_defconfig"
KBUILD_DEFCONFIG_adsp-sc584-ezkit = "sc584-ezkit_defconfig"
KBUILD_DEFCONFIG_adsp-sc573-ezkit = "sc573-ezkit_defconfig"
KBUILD_DEFCONFIG_adsp-sc589-mini = "sc589-mini_defconfig"

COMPATIBLE_MACHINE = "adsp-sc584-ezkit|adsp-sc589-ezkit|adsp-sc589-mini|adsp-sc573-ezkit"
