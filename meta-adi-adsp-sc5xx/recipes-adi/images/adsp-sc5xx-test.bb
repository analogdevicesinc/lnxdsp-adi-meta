require adsp-sc5xx-full.bb

SUMMARY = "Full image plus on-target test suites for Analog Devices ADSP-SC5xx boards"

inherit features_check

REQUIRED_DISTRO_FEATURES = "ptest"

IMAGE_INSTALL += "packagegroup-adsp-sc5xx-tests"
IMAGE_INSTALL:append:adsp-sc598-som-ezkit = " crypto-tests-ptest crc-tests-ptest"
IMAGE_ROOTFS_EXTRA_SPACE = "524288"
