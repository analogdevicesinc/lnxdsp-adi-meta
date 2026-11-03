SUMMARY = "DMA Copy Module"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://dmacopy_module.c;beginline=1;endline=3;md5=5439e3fcd222dfe499622b3561b119b0"

inherit module ptest

SRC_URI = " \
	file://Makefile \
	file://dmacopy_module.c \
	file://run-ptest \
"

S = "${UNPACKDIR}"
