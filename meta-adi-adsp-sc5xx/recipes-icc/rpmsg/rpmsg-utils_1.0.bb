SUMMARY = "RPMsg utils and test tools"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19aa4257230d63dcfd7e606df25b0c73"

SRC_URI = "\
	file://LICENSE \
	file://Makefile \
	file://rpmsg-bind-chardev.c \
	file://rpmsg-xmit.c \
	"

S = "${WORKDIR}"

do_install () {
	install -d ${D}${bindir}
	install -m 0755 rpmsg-bind-chardev ${D}${bindir}
	install -m 0755 rpmsg-xmit ${D}${bindir}
}
