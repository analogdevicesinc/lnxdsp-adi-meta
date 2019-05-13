inherit core-image extrausers
SUMMARY = "Minimal image for Analog Devices ADSP-SC58x boards"
LICENSE = "MIT"

OPROFILE_PERFORMANCE_BENCHMARK = "\
	dhrystone \
	whetstone-adi \
"

TOUCHSCREEN = "\
	tslib \
	evtest \
"

SQLITE = "\
	sqlite3 \
	sqlite-test \
"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-base \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    ${OPROFILE_PERFORMANCE_BENCHMARK} \
    ${TOUCHSCREEN} \
    ${SQLITE} \
    linuxptp \
    openssh \
    openssl \
    cpufrequtils \
    jpeg \
    libpng \
    giflib \
"

COMPATIBLE_MACHINE = "(adsp-sc584-ezkit|adsp-sc589-ezkit|adsp-sc589-mini)"

EXTRA_USERS_PARAMS = "usermod -P adi root;"

#BR2_PACKAGE_BUSYBOX_CONFIG="board/AnalogDevices/arm/busybox.config"
#BR2_PACKAGE_BUSYBOX_WATCHDOG=y
#BR2_PACKAGE_LIBTIRPC=y
#BR2_PACKAGE_NCURSES_TARGET_PANEL=y
#BR2_PACKAGE_NCURSES_TARGET_FORM=y
#BR2_PACKAGE_NCURSES_TARGET_MENU=y
#BR2_PACKAGE_IW=y
#BR2_PACKAGE_LRZSZ=y
#BR2_TARGET_ROOTFS_EXT2=y
#BR2_TARGET_ROOTFS_EXT2_3=y
#BR2_TARGET_ROOTFS_INITRAMFS=y
