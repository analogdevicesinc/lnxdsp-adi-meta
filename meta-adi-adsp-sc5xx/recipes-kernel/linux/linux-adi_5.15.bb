require linux-adi.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

DEPENDS += "u-boot-mkimage-native dtc-native"

PR = "r0"

PV = "5.15.78"

KERNEL_BRANCH ?= "master-5.15.78"
SRCREV  = "${AUTOREV}"

SRC_URI += "file://feature/"

HYBRID_AUDIO_PATCH = " \
	file://0001-SC598-Audio-configuration-for-hybrid-audio-support.patch \
"
SHARC_ALSA_PATCH = " \
	${HYBRID_AUDIO_PATCH} \
	file://0002-SC598-Enable-SHARC-ALSA-demo-disabling-most-linux-ba.patch \
"

SHARC_ALSA_PATCH_UBOOT = " \
	${SHARC_ALSA_PATCH} \
	file://0003-Disable-remoteproc.patch \
"

SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'adi_hybrid_audio', '${HYBRID_AUDIO_PATCH}', '', d)}"
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'adi_sharc_alsa_audio', '${SHARC_ALSA_PATCH}', '', d)}"
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'adi_sharc_alsa_audio_uboot', '${SHARC_ALSA_PATCH_UBOOT}', '', d)}"

# Include kernel configuration fragment
KERNEL_EXTRA_FEATURES ?= "${WORKDIR}/feature/cfg/nfs.cfg \
						  ${WORKDIR}/feature/cfg/wireless.cfg \
						  ${WORKDIR}/feature/cfg/cpufreq.cfg \
						  ${WORKDIR}/feature/cfg/crypto.cfg \
						  ${WORKDIR}/feature/cfg/tracepoints.cfg \
						  "

KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"

KERNEL_FEATURES_append_adsp-sc594_som_ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc589-ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc584-ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc573-ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc589-mini = " ${WORKDIR}/feature/snd_mini.scc"

do_install_append(){
	rm -rf ${D}/lib/modules/5.15.78-yocto-standard/modules.builtin.modinfo
}
