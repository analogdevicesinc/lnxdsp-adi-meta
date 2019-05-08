inherit adsp-sc58x-minimal

SUMMARY = "Full image for Analog Devices ADSP-SC58x boards"
LICENSE = "MIT"

IMAGE_INSTALL += " \
	alsa-utils \
	libmcapi \
	sc5xx-corecontrol \
"

TODO = " \
	oprofile \
	e2fsprogs \
	mtd \
	pciutils \
	freetype \
	pound \
	version \
	v4l2_video_test \
	video_test \ 
	bluez_utils \
	can-utils \
"

#BR2_TARGET_ROOTFS_JFFS2=y
#BR2_TARGET_ROOTFS_JFFS2_CUSTOM=y
#BR2_TARGET_ROOTFS_JFFS2_CUSTOM_PAGESIZE=0x100
#BR2_TARGET_ROOTFS_JFFS2_CUSTOM_EBSIZE=0x1000
#BR2_TARGET_ROOTFS_UBIFS=y


