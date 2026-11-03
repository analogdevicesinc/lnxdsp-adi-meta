SUMMARY = "GPTimer Test Module"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://hello_gptimer.c;beginline=1;endline=3;md5=5439e3fcd222dfe499622b3561b119b0"

inherit module ptest

SRC_URI = " \
	file://Makefile \
	file://hello_gptimer.c \
	file://run-ptest \
"

S = "${UNPACKDIR}"
