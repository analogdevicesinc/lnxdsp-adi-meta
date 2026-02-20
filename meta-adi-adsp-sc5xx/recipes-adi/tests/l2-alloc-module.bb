SUMMARY = "L2 Alloc Module"
LICENSE = "CLOSED"

inherit module

SRC_URI = " \
	file://Makefile \
	file://hello_l2_alloc.c \
"

S = "${WORKDIR}"
