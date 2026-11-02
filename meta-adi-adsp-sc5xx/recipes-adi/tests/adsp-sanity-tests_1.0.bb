SUMMARY = "Board-level sanity checks for ADSP-SC5xx and ADSP-SC8xx boards"
DESCRIPTION = "Checks the gptimer counters, RPMsg echo to the SHARC cores, the I2C buses, \
ALSA playback and capture, Ethernet and the PTP clock. Based on the lnxdsp-scripts sanity \
checks."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://run-ptest"
S = "${UNPACKDIR}"

INHIBIT_DEFAULT_DEPS = "1"

inherit ptest

PACKAGE_ARCH = "${MACHINE_ARCH}"
ALLOW_EMPTY:${PN} = "1"

# I2C buses that i2cdetect must not probe
ADI_I2C_SKIP_BUSES ?= ""
ADI_I2C_SKIP_BUSES:adsp-sc598-som-ezkit = "0"
ADI_I2C_SKIP_BUSES:adsp-sc598-som-ezlite = "0"

do_install_ptest() {
	sed -i 's/@ADI_I2C_SKIP_BUSES@/${ADI_I2C_SKIP_BUSES}/' ${D}${PTEST_PATH}/run-ptest
}

RDEPENDS:${PN}-ptest += "\
	alsa-utils-aplay \
	coreutils \
	i2c-tools \
	iperf3 \
	linuxptp \
	rpmsg-utils \
"

# The RPMsg checks need the echo firmware, which conflicts with the SHARC
# audio firmware. Without it they are skipped.
RDEPENDS:${PN}-ptest += "${@bb.utils.contains('DISTRO_FEATURES', 'linux_only_audio', 'rpmsg-echo-example', '', d)}"
