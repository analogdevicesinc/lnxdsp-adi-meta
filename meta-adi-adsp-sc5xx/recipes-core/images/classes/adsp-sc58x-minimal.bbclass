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
    iw \
    lrzsz \
    linuxptp \
    busybox-watchdog-init \
    pciutils \
"

COMPATIBLE_MACHINE = "(adsp-sc584-ezkit|adsp-sc589-ezkit|adsp-sc589-mini)"

EXTRA_USERS_PARAMS = "usermod -P adi root;"