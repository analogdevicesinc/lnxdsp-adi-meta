SUMMARY = "On-target test suites for ADSP-SC5xx boards"

inherit packagegroup features_check

REQUIRED_DISTRO_FEATURES = "ptest"

RDEPENDS:${PN} = " \
    ptest-runner \
    busybox-ptest \
    libseccomp-ptest \
    openssh-ptest \
    rt-tests-ptest \
    util-linux-ptest \
    ltp \
    stress-ng \
    memtester \
    fio \
    rng-tools \
"
