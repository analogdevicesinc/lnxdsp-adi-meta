inherit adsp-sc5xx

SUMMARY = "Full image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

do_image_wic[depends] += " \
        ${IMAGE_BASENAME}:do_image_ext4 \
"

OPROFILE_PERFORMANCE_BENCHMARK = "\
	dhrystone \
	whetstone \
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

#Removed (not building under Dunfell):
# gptimer-module
TEST_MODULES = "\
    dmacopy-module \
    l2-alloc-module \
    malloc-perf-test \
"

#Removed (not building under Dunfell):
# ${TEST_MODULES}
TESTING = "\
    linkport-test \
    rtc-test \
    gadgetfs-test \
    ltp \
	linuxptp \
	rtscts-test \
	sram-mmap-test \
"

BLUETOOTH_AND_HEADSET = " \
	alsa-utils \
	alsa-lib \
	rpmsg-utils \
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

#Removed for Dunfell:
# pound

EXTRA_IMAGE_FEATURES += "debug-tweaks dbg-pkgs tools-profile"

DEBUG = " \
    perf \
    uftrace \
    ltrace \
    strace \
"

IMAGE_INSTALL += " \
    ${OPROFILE_PERFORMANCE_BENCHMARK} \
    ${DEBUG} \
   	${TOUCHSCREEN} \
   	${SQLITE} \
   	${FILE_SYSTEM_TOOLS} \
   	${TESTING} \
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
	lighttpd \
	mtd-utils \
	mtd-utils-ubifs \
	pciutils \
	freetype \
	version \
	bonnie++ \
	libopus \
	opus-tools \
"
