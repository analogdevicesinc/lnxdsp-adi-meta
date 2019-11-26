inherit adsp-sc57x-minimal

SUMMARY = "Full image for Analog Devices ADSP-SC57x boards"
LICENSE = "MIT"

MCC = " \
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
	${MCC} \
	${BLUETOOTH_AND_HEADSET} \
	${OPROFILE} \
	${MPLAYER} \
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
	rtscts-test \
"
