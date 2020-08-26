require linux-adi.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

PR = "r0"

KERNEL_BRANCH ?= "develop/yocto-1.0.0-fastboot"
SRCREV  = "${AUTOREV}"

SRC_URI += "file://feature/"

# Include kernel configuration fragment
KERNEL_EXTRA_FEATURES ?= "feature/cfg/nfs.cfg \
						  feature/cfg/wireless.cfg \
						  feature/cfg/cpufreq.cfg \
						  feature/cfg/crypto.cfg \
						  "
#KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"

KERNEL_FEATURES_append_adsp-sc589-ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc584-ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc573-ezkit = " feature/snd_ezkit.scc"
#KERNEL_FEATURES_append_adsp-sc589-mini = " feature/snd_mini.scc"

kernel_do_deploy_append() {
	if [ ! -z "${INITRAMFS_IMAGE}" -a x"${INITRAMFS_IMAGE_BUNDLE}" = x1 ]; then
		for imageType in ${KERNEL_IMAGETYPES} ; do
			initramfs_base_name=${imageType}-${INITRAMFS_NAME}
			ln -sf ${initramfs_base_name}.bin $deployDir/${imageType}
		done
	fi
}
INITRAMFS_IMAGE = "adsp-sc5xx-ramdisk"