inherit adsp-sc5xx

SUMMARY = "Full image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

do_image_wic[depends] += " \
        ${IMAGE_BASENAME}:do_image_ext4 \
"


FILE_SYSTEM_TOOLS = "\
	e2fsprogs \
"

TESTING = "\
    sram-mmap-test \
"

SOUND = " \
	alsa-utils \
	alsa-lib \
	rpmsg-utils \
	dbus \
	play \
"

UTILS = " \
    iperf3 \
    netperf \
    cpufrequtils \
    uftrace \
    ltrace \
    strace \
    bonnie++ \
    python3 \
"

IMAGE_INSTALL += " \
    ${UTILS} \
   	${FILE_SYSTEM_TOOLS} \
   	${TESTING} \
	${SOUND} \
	ltp \
	linuxptp \
	linux-firmware-rtl8192su \
	linux-firmware-adau1761 \
	mtd-utils \
	mtd-utils-ubifs \
	version \
	libopus \
	opus-tools \
"
