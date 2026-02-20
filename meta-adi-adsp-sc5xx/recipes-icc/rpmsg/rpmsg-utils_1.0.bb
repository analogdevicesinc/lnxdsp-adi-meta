SUMMARY = "RPMsg utils and test tools"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=16b1fc955d97a04b4b9c9ea9ae377283"

SRCREV = "631bbf39884d85fcc1f06fba1b51ac90431f4bf7"
PV = "0.1+git${SRCPV}"
SRC_URI = "git://github.com/analogdevicesinc/rpmsg-utils.git;protocol=https;branch=release/yocto-3.1.0"

S = "${WORKDIR}/git"

do_install () {
	install -d ${D}${bindir}
	install -m 0755 rpmsg-bind-chardev ${D}${bindir}
	install -m 0755 rpmsg-xmit ${D}${bindir}
	install -m 0755 rpmsg-xmit-p ${D}${bindir}
}
