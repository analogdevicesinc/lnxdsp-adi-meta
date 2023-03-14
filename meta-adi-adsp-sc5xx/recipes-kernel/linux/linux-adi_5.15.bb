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

# Include kernel configuration fragments
KERNEL_FEATURES:append = " \
	feature/cfg/nfs.cfg \
	feature/cfg/wireless.cfg \
	feature/cfg/cpufreq.cfg \
	feature/cfg/crypto.cfg \
	feature/cfg/tracepoints.cfg \
"

KERNEL_FEATURES:append_adsp-sc594_som_ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES:append_adsp-sc589-ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES:append_adsp-sc584-ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES:append_adsp-sc573-ezkit = " feature/snd_ezkit.scc"
KERNEL_FEATURES:append_adsp-sc589-mini = " feature/snd_mini.scc"

do_install:append(){
	rm -rf ${D}/lib/modules/5.15.78-yocto-standard/modules.builtin.modinfo
}
