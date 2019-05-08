DESCRIPTION = "Analog Devices MCAPI Library"
LICENSE = "CLOSED"

inherit autotools

SRC_URI = " \
	svn://svn.code.sf.net/p/adi-openapp/code/trunk/libs;module=libmcapi;protocol=http;rev=HEAD \
	file://0001-Applied-0001-add_wait_block_function_for_MCAPI.patch.patch \
	file://0002-Applied-0002-add_msg_transaction_demo_and_test_for_M.patch \
	file://0003-Applied-0003_cover_cache_check_for_MultiCore_MCAPI_d.patch \
	file://0004-Declare-mcapi_dprintf-static-for-GCC8.patch \
"

S = "${WORKDIR}/libmcapi/libmcapi-2.0"

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