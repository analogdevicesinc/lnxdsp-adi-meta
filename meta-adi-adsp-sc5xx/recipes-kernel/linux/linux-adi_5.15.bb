USB_AUDIO = " \
	${@bb.utils.contains('DISTRO_FEATURES', 'adi_usb_gadget_audio', 'adi_usb_gadget_audio.inc', '', d)} \
"

require linux-adi.inc sharc_audio.inc ${USB_AUDIO}

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

DEPENDS += "u-boot-mkimage-native dtc-native"

PV = "5.15.78"

LINUX_VERSION = "${PV}"

KERNEL_BRANCH ?= "develop/yocto-3.1.0"
SRCREV  = "376fa91e308afa51408be0f1561b15b7cfd7a69d"

# Include kernel configuration fragments
SRC_URI:append = " \
	file://feature/cfg/nfs.cfg \
	file://feature/cfg/wireless.cfg \
	file://feature/cfg/cpufreq.cfg \
	file://feature/cfg/crypto.cfg \
	file://feature/cfg/tracepoints.cfg \
"

SRC_URI:append:adsp-sc594-som-ezkit = " file://feature/snd_ezkit.scc"
SRC_URI:append:adsp-sc589-ezkit = " file://feature/snd_ezkit.scc"
SRC_URI:append:adsp-sc584-ezkit = " file://feature/snd_ezkit.scc"
SRC_URI:append:adsp-sc573-ezkit = " file://feature/snd_ezkit.scc"
SRC_URI:append:adsp-sc589-mini = " file://feature/snd_mini.scc"

SRC_URI:append:adsp-sc598-som-ezkit = "${@' file://0001-sc598-som-enable-SDcard.patch' if bb.utils.to_boolean(d.getVar('ADSP_SC598_SDCARD')) else ''}"

do_install:append(){
	rm -rf ${D}/lib/modules/5.15.78-yocto-standard/modules.builtin.modinfo
}
