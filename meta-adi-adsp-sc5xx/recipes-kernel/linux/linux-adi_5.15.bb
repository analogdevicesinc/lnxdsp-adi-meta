require linux-adi.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

DEPENDS += "u-boot-mkimage-native dtc-native"

PR = "r0"

PV = "5.15.78"

LINUX_VERSION = "${PV}"

KERNEL_BRANCH ?= "develop/yocto-3.0.0"
SRCREV  = "${AUTOREV}"

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
SRC_URI:append = " \
	file://feature/cfg/nfs.cfg \
	file://feature/cfg/wireless.cfg \
	file://feature/cfg/cpufreq.cfg \
	file://feature/cfg/crypto.cfg \
	file://feature/cfg/tracepoints.cfg \
"

SRC_URI:append_adsp-sc594_som_ezkit = "file://feature/snd_ezkit.scc"
SRC_URI:append_adsp-sc589-ezkit = "file://feature/snd_ezkit.scc"
SRC_URI:append_adsp-sc584-ezkit = "file://feature/snd_ezkit.scc"
SRC_URI:append_adsp-sc573-ezkit = "file://feature/snd_ezkit.scc"
SRC_URI:append_adsp-sc589-mini = "file://feature/snd_mini.scc"

do_install:append(){
	rm -rf ${D}/lib/modules/5.15.78-yocto-standard/modules.builtin.modinfo
}
