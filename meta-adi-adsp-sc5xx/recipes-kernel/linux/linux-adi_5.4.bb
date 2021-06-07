require linux-adi.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

PR = "r0"

KERNEL_BRANCH ?= "v5.4-rebase-wip"
SRCREV  = "${AUTOREV}"

SRC_URI += "file://feature/"

# Include kernel configuration fragment
KERNEL_EXTRA_FEATURES ?= "feature/cfg/nfs.cfg \
						  feature/cfg/wireless.cfg \
						  feature/cfg/cpufreq.cfg \
						  feature/cfg/crypto.cfg \
						  "
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"

KERNEL_FEATURES_append_adsp-sc594_som_ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc589-ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc584-ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc573-ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc589-mini = " feature/snd_mini.scc"

do_install_append(){
	rm -rf ${D}/lib/modules/5.4.0-yocto-standard/modules.builtin.modinfo
}
