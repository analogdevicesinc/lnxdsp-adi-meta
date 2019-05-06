SECTION = "kernel"
DESCRIPTION = "Linux kernel for Analog Devices ADSP-5xx processors"

inherit kernel

# Pull in the devicetree files into the rootfs
# RDEPENDS_kernel-base += "kernel-devicetree"

KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

S = "${WORKDIR}/git"


# Append to the MACHINE_KERNEL_PR so that a new SRCREV will cause a rebuild
MACHINE_KERNEL_PR_append = "a"
PR = "${MACHINE_KERNEL_PR}"

KERNEL_GIT_URI = "git://git@src.timesys.com/services/analog-devices/analog-devices-yocto-bsp-porting/linux-4.16.git"
KERNEL_GIT_PROTOCOL = "ssh"

SRC_URI += " \
	${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${BRANCH} \
"

do_configure_append(){
	make ${KERNEL_MACHINE}
}

COMPATIBLE_MACHINE = "adsp-sc584-ezkit|adsp-sc589-ezkit|adsp-sc589-mini|adsp-sc573-ezkit"