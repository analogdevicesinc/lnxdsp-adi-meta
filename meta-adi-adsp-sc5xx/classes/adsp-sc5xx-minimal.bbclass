inherit adsp-sc5xx

SUMMARY = "Minimal image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

do_image_wic[depends] += " \
	${IMAGE_BASENAME}:do_image_ext4 \
"