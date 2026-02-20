SUMMARY = "GPTimer Test Module"
LICENSE = "CLOSED"

inherit module

SRC_URI = " \
	file://Makefile \
	file://hello_gptimer.c \
"

S = "${WORKDIR}"