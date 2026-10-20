inherit adsp-ptest-cleanup

# Tests that cannot pass on the ADSP-SC5xx boards, mostly because they need kernel
# options the ADI configurations don't enable.
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

# Remove the tests rather than extending run.sh --exclude, which matches by substring
# (excluding lsfd/mkfds-raw6 would also skip lsfd/mkfds-raw).
do_install_ptest:append() {
    for t in ${UTIL_LINUX_PTEST_EXCLUDE}; do
        [ -e ${D}${PTEST_PATH}/tests/ts/$t ] || bbfatal "util-linux test $t no longer exists"
        rm -rf ${D}${PTEST_PATH}/tests/ts/$t
    done
}
