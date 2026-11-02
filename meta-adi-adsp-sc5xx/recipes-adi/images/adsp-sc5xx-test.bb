require adsp-sc5xx-full.bb

SUMMARY = "Full image plus on-target test suites for Analog Devices ADSP-SC5xx boards"

inherit features_check

REQUIRED_DISTRO_FEATURES = "ptest"

IMAGE_INSTALL += "packagegroup-adsp-sc5xx-tests"
IMAGE_INSTALL += " \
    adsp-dmacopy-test-ptest \
    adsp-gptimer-test-ptest \
    adsp-l2-alloc-test-ptest \
    adsp-sanity-tests-ptest \
    adsp-sram-mmap-test-ptest \
    crc-tests-ptest \
"
# The ADSP-SC598 and ADSP-SC846 have no mmio-sram pools for adsp-l2-alloc-test
IMAGE_INSTALL:remove:adsp-sc598-som-ezkit = "adsp-l2-alloc-test-ptest"
IMAGE_INSTALL:remove:adsp-sc598-som-ezlite = "adsp-l2-alloc-test-ptest"
IMAGE_INSTALL:remove:adsp-sc846-som-ezkit = "adsp-l2-alloc-test-ptest"
IMAGE_INSTALL:append:adsp-sc598-som-ezkit = " crypto-tests-ptest"
IMAGE_ROOTFS_EXTRA_SPACE = "524288"
