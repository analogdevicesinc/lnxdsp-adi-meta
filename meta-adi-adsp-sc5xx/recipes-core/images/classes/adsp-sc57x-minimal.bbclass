inherit core-image extrausers

SUMMARY = "Minimal image for Analog Devices ADSP-SC57x boards"
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
    l2-module \
"

TESTING = "\
    ${TEST_MODULES} \
    linux-adi-selftests \
    linkport-test \
    rtc-test \
    gadgetfs-test \
    ltp \
"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-base \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    ${OPROFILE_PERFORMANCE_BENCHMARK} \
    ${TOUCHSCREEN} \
    ${SQLITE} \
    ${FILE_SYSTEM_TOOLS} \
    ${TESTING} \
    openssh \
    openssl \
    cpufrequtils \
    jpeg \
    libpng \
    giflib \
    libtirpc \
    ncurses \
    wireless-tools \
    linux-firmware-rtl8192su \
    lrzsz \
    linuxptp \
    busybox-watchdog-init \
    util-linux \
"

COMPATIBLE_MACHINE = "(adsp-sc573-ezkit)"

EXTRA_USERS_PARAMS = "usermod -P adi root;"
