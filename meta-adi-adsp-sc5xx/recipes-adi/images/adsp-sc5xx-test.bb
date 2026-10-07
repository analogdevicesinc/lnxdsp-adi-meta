require adsp-sc5xx-full.bb

SUMMARY = "Full image plus on-target test suites for Analog Devices ADSP-SC5xx boards"

IMAGE_INSTALL += "packagegroup-adsp-sc5xx-tests"
