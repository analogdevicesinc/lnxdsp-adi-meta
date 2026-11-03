SUMMARY = "L2 Alloc Module"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://hello_l2_alloc.c;beginline=1;endline=3;md5=4cec501f5b01a13580b5c7c05144d2cf"

inherit module ptest

SRC_URI = " \
	file://Makefile \
	file://hello_l2_alloc.c \
	file://run-ptest \
"

S = "${UNPACKDIR}"
