HYBRID_AUDIO_PATCH = " \
	file://0001-SC598-Audio-configuration-for-hybrid-audio-support.patch \
"
SHARC_ALSA_PATCH = " \
	${HYBRID_AUDIO_PATCH} \
	file://0002-SC598-Enable-SHARC-ALSA-demo-disabling-most-linux-ba.patch \
"

SHARC_ALSA_WITHOUT_RPMSG = " \
	${SHARC_ALSA_PATCH} \
	file://0003-Disable-sharc-n-_rpmsg-drivers-and-revert-RPMSG_ADI-.patch \
"

SHARC_ALSA_PATCH_UBOOT = " \
	${SHARC_ALSA_PATCH} \
	file://0003-Disable-remoteproc.patch \
"

SRC_URI += " \
	${@bb.utils.contains('DISTRO_FEATURES', 'adi_hybrid_audio', '${HYBRID_AUDIO_PATCH}', '', d)} \
"

SRC_URI += " \
	${@bb.utils.contains('DISTRO_FEATURES', 'adi_sharc_alsa_audio', '${SHARC_ALSA_WITHOUT_RPMSG}', '', d)} \
"

SRC_URI += " \
	${@bb.utils.contains('DISTRO_FEATURES', 'adi_sharc_alsa_audio_uboot', '${SHARC_ALSA_PATCH_UBOOT}', '', d)} \
"
