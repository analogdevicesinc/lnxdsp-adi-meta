DESCRIPTION = "Analog Devices MCAPI Library"
LICENSE = "CLOSED"

inherit autotools

MCAPI_GIT_URI ?= "git://github.com/analogdevicesinc/lnxdsp-mcapi.git"
MCAPI_GIT_PROTOCOL ?= "https"
MCAPI_GIT_BRANCH ?= "develop/yocto-1.0.0"

PR = "r0"
SRCREV= "${AUTOREV}"

SRC_URI = " \
${MCAPI_GIT_URI};protocol=${MCAPI_GIT_PROTOCOL};branch=${MCAPI_GIT_BRANCH}"

S = "${WORKDIR}/git"

DEPENDS = "kernel-devsrc linux-adi"

EXTRA_OEMAKE += ' CFLAGS="${CFLAGS} -I${STAGING_KERNEL_DIR}/arch/blackfin/include -I${STAGING_KERNEL_DIR}/drivers/staging/icc/include"'

do_configure_prepend () {
	cd ${S} && ./autogen.sh
}

do_compile_prepend(){
	cd ${S}
}

do_install(){
	install -d ${D}/usr/bin
	install -d ${D}/usr/lib	
	install -m 755 ${S}/tests/.libs/* ${D}/usr/bin/
	install -m 755 ${S}/.libs/libmcapi.so.0.0.0 ${D}/usr/lib/
	ln -s libmcapi.so.0.0.0 ${D}/usr/lib/libmcapi.so.0 
	ln -s libmcapi.so.0.0.0 ${D}/usr/lib/libmcapi.so 
}