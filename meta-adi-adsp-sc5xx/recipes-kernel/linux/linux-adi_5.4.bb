require linux-adi.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

PR = "r0"

KERNEL_BRANCH ?= "beta/sc589"
SRCREV  = "c87fc8c25d4950d283d2271ae5802a45edbb0ea4"

PV = "5.4.162"
LINUX_VERSION = "5.4.162"

SRC_URI += "file://feature/"

# Include kernel configuration fragment
KERNEL_EXTRA_FEATURES ?= "${WORKDIR}/feature/cfg/nfs.cfg \
						  ${WORKDIR}/feature/cfg/wireless.cfg \
						  ${WORKDIR}/feature/cfg/cpufreq.cfg \
						  ${WORKDIR}/feature/cfg/crypto.cfg \
						  "

def get_sharc_alsa (d):
    if d.getVar('ANALOG_DEVICES_SHARC_ALSA'):
        return " ${WORKDIR}/feature/cfg/sound/sc5xx_sharc_alsa.cfg"
    else:
        return ""

KERNEL_EXTRA_FEATURES += "${@get_sharc_alsa(d)}"

KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"

KERNEL_FEATURES_append_adsp-sc594_som_ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc589-ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc584-ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc573-ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc589-mini = " ${WORKDIR}/feature/snd_mini.scc"

do_install_append(){
	rm -rf ${D}/lib/modules/5.4.0-yocto-standard/modules.builtin.modinfo
}
