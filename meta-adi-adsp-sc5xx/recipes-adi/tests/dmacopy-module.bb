SUMMARY = "DMA Copy Module"
LICENSE = "CLOSED"

inherit module

SRC_URI = " \
	file://Makefile \
	file://dmacopy_module.c \
"

S = "${WORKDIR}"