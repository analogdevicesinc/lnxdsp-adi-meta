SUMMARY = "Gadget FS Test"
LICENSE = "CLOSED"

inherit module

SRC_URI = " \
	http://mirror.egtvedt.no/avr32linux.org/twiki/pub/Main/GadgetFsTest/gadgetfs-test.tar.bz2 \
	file://0001-Apply-0001-fix-usb-ch9-include.patch.patch \
	file://0002-Apply-0002-rename-include-usb_gadgetfs-to-usb-dir.pa.patch \
	file://0003-Apply-gadgetfs-test-fix-musb-device-name.patch.patch \
	file://0004-Apply-gadgetfs-test-fix-musb-endpoint.patch.patch \
"

SRC_URI[md5sum] = "49476a74c29f1281c8a4c035aa57a5bd"
SRC_URI[sha256sum] = "bd8ebcf7ce86f4b022a4e7ba6b1cc16ffc4022bb58c1910fe4ac96c88217e7ec"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

do_compile(){
	cd ${B}
	${CC} ${CFLAGS} ${LDFLAGS} -c -o ${B}/usbstring.o ${S}/gadgetfs-test/usbstring.c -I${S}/gadgetfs-test
	${CC} ${CFLAGS} ${LDFLAGS} -c -o ${B}/usb.o ${S}/gadgetfs-test/usb.c -I${S}/gadgetfs-test
	${CC} ${CFLAGS} ${LDFLAGS} -o ${B}/gadgetfs-test ${B}/usb.o ${B}/usbstring.o -lpthread
}

do_install(){
	install -d ${D}/usr/bin
	install -m 755 ${B}/gadgetfs-test ${D}/usr/bin/
}

FILES:${PN} += " \
	/usr/bin \
	/usr/bin/gadgetfs-test \
"
