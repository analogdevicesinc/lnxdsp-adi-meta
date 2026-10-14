inherit adsp-ptest-cleanup

# Tests that need kernel options the ADI configurations don't enable (RTC, loop, VSOCK,
# SCHED_DEADLINE bandwidth) or that fail on an NFSv3 root (leases, RENAME_EXCHANGE).
UTIL_LINUX_PTEST_EXCLUDE ?= " \
    exch/exch \
    hwclock/show \
    lsfd/column-deleted \
    lsfd/column-name \
    lsfd/column-xmode \
    lsfd/error-eperm \
    lsfd/mkfds-raw6 \
    lsfd/mkfds-ro-block-device \
    lsfd/mkfds-vsock \
    lsfd/option-hyperlink \
    lslocks/lslocks \
    mount/fallback \
    schedutils/chrt \
"

do_install_ptest:append() {
    grep -q -- "--exclude='" ${D}${PTEST_PATH}/run-ptest || bbfatal "run-ptest no longer has an --exclude list to extend"
    sed -i "s#--exclude='\([^']*\)'#--exclude='\1 $(echo ${UTIL_LINUX_PTEST_EXCLUDE})'#" ${D}${PTEST_PATH}/run-ptest
}
