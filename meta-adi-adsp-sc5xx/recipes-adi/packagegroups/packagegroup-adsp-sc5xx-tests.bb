SUMMARY = "On-target test suites for ADSP-SC5xx boards"

inherit packagegroup features_check

REQUIRED_DISTRO_FEATURES = "ptest"

RDEPENDS:${PN} = " \
    ptest-runner \
    openssl-ptest \
    util-linux-ptest \
    libgpiod-ptest \
    rt-tests-ptest \
    ltp \
    stress-ng \
    memtester \
    fio \
    rng-tools \
"
