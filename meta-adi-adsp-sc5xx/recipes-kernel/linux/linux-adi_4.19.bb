require linux-adi.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

PR = "r0"

SRCBRANCH = "develop/yocto-1.0.0"
SRCREV  = "${AUTOREV}"

SRAM_ALLOC_PATCH = "${@ 'file://Add-l2-alloc-module.patch' if d.getVar('ANALOG_DEVICES_SRAM_ALLOC') else ''}"

SRC_URI += " \
	file://0001-Fix-errors-for-compiling-with-GCC8.patch \
"

# Include kernel configuration fragment
KERNEL_EXTRA_FEATURES ?= "feature/cfg/nfs.cfg \
						  feature/cfg/wireless.cfg \
						  feature/cfg/cpufreq.cfg \
						  feature/cfg/crypto.cfg \
						  "
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"

KERNEL_FEATURES_append_adsp-sc589-ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc584-ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc573-ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc589-mini = " feature/snd_mini.scc"
