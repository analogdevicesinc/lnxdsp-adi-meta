USB_AUDIO="\
	${@bb.utils.contains('DISTRO_FEATURES', 'adi_usb_gadget_audio', 'adi_usb_gadget_audio.inc', '', d)} \
"
require linux-adi.inc sharc_audio.inc ${USB_AUDIO}

LICENSE="GPL-2.0-only"
LIC_FILES_CHKSUM="file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

DEPENDS += "u-boot-mkimage-native dtc-native"

# Include kernel configuration fragments
SRC_URI:append="\
	file://feature/cfg/nfs.cfg \
	file://feature/cfg/wireless.cfg \
	file://feature/cfg/cpufreq.cfg \
	file://feature/cfg/crypto.cfg \
	file://feature/cfg/tracepoints.cfg \
"

PV="6.12"
KERNEL_BRANCH = "adsp-main-6.12"

SRCREV="${AUTOREV}"
KERNEL_VERSION_SANITY_SKIP = "1"
LINUX_VERSION="${PV}"


SRC_URI:append:adsp-sc594-som-ezkit = " file://feature/cfg/snd_ezkit.scc"
SRC_URI:append:adsp-sc589-ezkit = " file://feature/cfg/snd_ezkit.scc"
SRC_URI:append:adsp-sc584-ezkit = " file://feature/cfg/snd_ezkit.scc"
SRC_URI:append:adsp-sc573-ezkit = " file://feature/cfg/snd_ezkit.scc"
SRC_URI:append:adsp-sc589-mini = " file://feature/cfg/snd_mini.scc"

#@todo: Check for SDcard support in the kernel
#SRC_URI:append:adsp-sc598-som-ezkit = "${@' file://0001-sc598-som-enable-SDcard.patch' if (bb.utils.to_boolean(d.getVar('ADSP_SC598_SDCARD')) and (d.getVar('ADSP_KERNEL_TYPE') != 'upstream')) else ''}"

SRC_URI:append:adsp-sc598-som-ezkit = "${@bb.utils.contains_any('MACHINE_FEATURES', 'falcon', 'file://0001-Disabling-peripherals-for-a-faster-falcon-boot.patch', '', d)}"

SRC_URI:append:adsp-sc598-som-ezkit = ' file://0001-SC598-fix-stmmac-dma-split-header-crash.patch'

SRC_URI:append = " file://0001-enable-Loop-Devices-and-Swap-over-NFS.patch "

do_install:append(){
	rm -rf ${D}/lib/modules/*-yocto-standard/modules.builtin.modinfo
}
