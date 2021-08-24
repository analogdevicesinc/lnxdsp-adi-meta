inherit adsp-sc5xx

SUMMARY = "Full image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

def get_whetstone (d):
  WHETSTONE = ""
  MACHINE = d.getVar('MACHINE')
  if MACHINE == 'adsp-sc598-som-ezkit':
    WHETSTONE = "whetstone"
  else:
    WHETSTONE = "whetstone-adi"
  return WHETSTONE

OPROFILE_PERFORMANCE_BENCHMARK = "\
	dhrystone \
	${@get_whetstone(d)} \
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
	sharc-alsa-lib \
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

QT5 = " \
	qtbase \
	qttools \
	qtquickcontrols \
	qtquickcontrols2 \
	qtquick3d \
"

QT5_DEMOS = " \
	cinematicexperience \
	qtsmarthome \
	quitbattery \
	quitindicators \
	qt5nmapcarousedemo \
	qt5nmapper \
	qt5everywheredemo \
	qt5ledscreen \
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
	${QT5} \
	${QT5_DEMOS} \
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
