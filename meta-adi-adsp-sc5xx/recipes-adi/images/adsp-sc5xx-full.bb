inherit adsp-sc5xx

SUMMARY = "Full image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

OPROFILE_PERFORMANCE_BENCHMARK = "\
	dhrystone \
	whetstone-adi \
"

TOUCHSCREEN = "\
	tslib \
	tslib-calibrate \
	evtest \
"

SQLITE = "\
	sqlite3 \
	sqlite-test \
"

FILE_SYSTEM_TOOLS = "\
	dosfstools \
	e2fsprogs \
"

TEST_MODULES = "\
    dmacopy-module \
    gptimer-module \
    l2-alloc-module \
    malloc-perf-test \
"

TESTING = "\
    ${TEST_MODULES} \
    linux-adi-selftests \
    linkport-test \
    rtc-test \
    gadgetfs-test \
    ltp \
	linuxptp \
	rtscts-test \
"

ICC = " \
	libmcapi \
	sc5xx-corecontrol \
"

BLUETOOTH_AND_HEADSET = " \
	alsa-utils \
	alsa-lib \
	dbus \
	bluez5 \
	packagegroup-tools-bluetooth \
	expat \
	play \
"

OPROFILE = " \
	oprofile \
"

MPLAYER = " \
	mplayer-common \
	mpv \
	video-test \
	v4l2-video-test \
	ffmpeg \
"

IMAGE_INSTALL += " \
    ${OPROFILE_PERFORMANCE_BENCHMARK} \
   	${TOUCHSCREEN} \
   	${SQLITE} \
   	${FILE_SYSTEM_TOOLS} \
   	${TESTING} \
	${ICC} \
	${BLUETOOTH_AND_HEADSET} \
	${OPROFILE} \
	${MPLAYER} \
	cpufrequtils \
	jpeg \
	libpng \
	giflib \
	libtirpc \
	linux-firmware-rtl8192su \
	linux-firmware-adau1761 \
	lrzsz \
	wireless-tools \
	can-utils \
	netperf \
	pound \
	lighttpd \
	mtd-utils \
	mtd-utils-ubifs \
	pciutils \
	freetype \
	version \
	bonnie++ \
"
