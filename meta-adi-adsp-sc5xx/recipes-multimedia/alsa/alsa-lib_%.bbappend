FILESEXTRAPATHS:prepend := "${THISDIR}/alsa-lib:"

SRC_URI += " \
	file://alsa-lib-01-adi-audio-codec-config.patch \
	file://alsa-lib-02-pcm-ucm.patch \
	file://alsa-lib-04-change-cards-config-file-name.patch \
	file://alsa-lib-05-add-adi-audio-codec-configs-to-cfg_files.patch \
	file://alsa-lib-06-add-sc58x-codec.patch \
"
