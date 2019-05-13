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

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-base \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    ${OPROFILE_PERFORMANCE_BENCHMARK} \
    ${TOUCHSCREEN} \
    ${SQLITE} \
    ${FILE_SYSTEM_TOOLS} \
    linuxptp \
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
    busybox-watchdog-init \
"

COMPATIBLE_MACHINE = "(adsp-sc584-ezkit|adsp-sc589-ezkit|adsp-sc589-mini)"

EXTRA_USERS_PARAMS = "usermod -P adi root;"