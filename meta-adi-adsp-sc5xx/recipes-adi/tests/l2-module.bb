SUMMARY = "L2 Module"
LICENSE = "CLOSED"

inherit module

SRC_URI = " \
	file://Makefile \
	file://hello.c \
"

S = "${WORKDIR}"