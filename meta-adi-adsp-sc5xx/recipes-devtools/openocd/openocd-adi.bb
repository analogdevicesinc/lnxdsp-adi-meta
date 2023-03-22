SUMMARY = "Free and Open On-Chip Debugging, In-System Programming and Boundary-Scan Testing"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "libusb-compat"
RDEPENDS:${PN} = "libusb1"

OPENOCD_GIT_URI ?= "git://github.com/analogdevicesinc/openocd.git"
OPENOCD_GIT_PROTOCOL ?= "https"
OPENOCD_BRANCH ?= "adi-main"

SRC_URI = " \
	${OPENOCD_GIT_URI};protocol=${OPENOCD_GIT_PROTOCOL};branch=${OPENOCD_BRANCH} \
	file://0001-Fixup-build-error-by-splitting-dap_cmd_pool-into-dap.patch \
"

SRCREV = "d5c76126458b8dc7bff7a30b48198801c05143e8"

PV = "0.10+gitr${SRCPV}"
S = "${WORKDIR}/git"

inherit pkgconfig autotools-brokensep gettext

BBCLASSEXTEND += "native nativesdk"

EXTRA_OECONF = "--disable-ftdi --disable-stlink --disable-ti-icdi --disable-ulink --disable-usb-blaster-2 --disable-ft232r \
                --disable-vsllink --enable-jlink --disable-xds110 --disable-osbdm --disable-opendous --disable-aice \
                --disable-usbprog --disable-rlink --disable-armjtagew --enable-maintainer-mode --enable-ice1000 --enable-ice2000 \
                --enable-adi-dbgagent --disable-libusbmux"

do_configure() {
    ./bootstrap
    oe_runconf ${EXTRA_OECONF}
}

EXTRA_OEMAKE = 'CPPFLAGS="-O2 -Wno-error" CFLAGS="-O2 -Wno-error"'

do_install() {
    oe_runmake ${EXTRA_OEMAKE} DESTDIR=${D} install
    if [ -e "${D}${infodir}" ]; then
      rm -Rf ${D}${infodir}
    fi
    if [ -e "${D}${mandir}" ]; then
      rm -Rf ${D}${mandir}
    fi
    if [ -e "${D}${bindir}/.debug" ]; then
      rm -Rf ${D}${bindir}/.debug
    fi
}

FILES:${PN} = " \
  ${datadir}/openocd/* \
  ${bindir}/openocd \
  "
