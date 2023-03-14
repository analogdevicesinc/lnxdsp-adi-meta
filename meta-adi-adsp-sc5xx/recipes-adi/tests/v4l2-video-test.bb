DESCRIPTION = "Analog Devices V4L2 Video Test Utility"
LICENSE = "CLOSED"

SRC_URI = " \
       svn://svn.code.sf.net/p/adi-openapp/code/trunk/tests;module=v4l2_video_test;protocol=http;rev=HEAD \
       file://0001-Parse-video-image_size-to-kernel-buf.patch \
"

S = "${WORKDIR}/v4l2_video_test"

do_compile:prepend(){
	cd ${S}
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${S}/v4l2_video_capture ${D}/usr/bin/
	install -m 755 ${S}/v4l2_video_display ${D}/usr/bin/
	install -m 755 ${S}/v4l2_video_loopback ${D}/usr/bin/
}
