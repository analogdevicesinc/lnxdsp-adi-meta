SUMMARY = "On-target test suites for ADSP-SC5xx boards"

inherit packagegroup features_check

REQUIRED_DISTRO_FEATURES = "ptest"

# bzip2: the busybox bunzip2/bzcat ptests compress their input with it
RDEPENDS:${PN} = " \
    ptest-runner \
    busybox-ptest \
    bzip2 \
    libseccomp-ptest \
    openssh-ptest \
    rt-tests-ptest \
    util-linux-ptest \
    stress-ng \
    memtester \
    fio \
    rng-tools \
"
