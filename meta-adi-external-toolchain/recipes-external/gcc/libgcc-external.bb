SUMMARY = "The GNU Compiler Collection - libgcc"
HOMEPAGE = "http://www.gnu.org/software/gcc/"
SECTION = "devel"
DEPENDS += "virtual/${TARGET_PREFIX}binutils"
PV = "${GCC_VERSION}"

inherit external-toolchain

LICENSE = "GPL-3.0-with-GCC-exception"

# libgcc needs libc, but glibc's utilities need libgcc, so short-circuit the
# interdependency here by manually specifying it rather than depending on the
# libc packagedata.
RDEPENDS_${PN} += "${@'${PREFERRED_PROVIDER_virtual/libc}' if '${PREFERRED_PROVIDER_virtual/libc}' else '${TCLIBC}'}"
INSANE_SKIP_${PN} += "build-deps file-rdeps"

# The dynamically loadable files belong to libgcc, since we really don't need the static files
# on the target, moreover linker won't be able to find them there (see original libgcc.bb recipe).
BINV = "${GCC_VERSION}"

do_analog_devices(){
    #Copy platform specific libraries in to main library path
    cp ${S}/image/lib/${ANALOG_DEVICES_PLATFORM_LIBDIR}/* ${S}/image/lib/
}

addtask do_analog_devices after do_install before do_populate_sysroot

FILES_LIST = " \
    ${base_libdir}/${ANALOG_DEVICES_PLATFORM_LIBDIR}/* \
    ${base_libdir}/libgomp.so.1 \
    ${base_libdir}/libitm.spec \
    ${base_libdir}/libatomic.so.1.0.0 \
    ${base_libdir}/libssp.so \
    ${base_libdir}/libasan.so \
    ${base_libdir}/libitm.so.1.0.0 \
    ${base_libdir}/libasan.so.0 \
    ${base_libdir}/libssp.so.0.0.0 \
    ${base_libdir}/libasan.so.0.0.0 \
    ${base_libdir}/libmudflap.so.0.0.0 \
    ${base_libdir}/libatomic.so \
    ${base_libdir}/libgomp.spec \
    ${base_libdir}/libstdc++.so \
    ${base_libdir}/libmudflapth.so \
    ${base_libdir}/libstdc++.so.6.0.19-gdb.py \
    ${base_libdir}/libatomic.a \
    ${base_libdir}/libmudflapth.so.0.0.0 \
    ${base_libdir}/libasan_preinit.o \
    ${base_libdir}/libgomp.so.1.0.0 \
    ${base_libdir}/libstdc++.a \
    ${base_libdir}/libssp.so.0 \
    ${base_libdir}/libstdc++.so.6 \
    ${base_libdir}/libgcc_s.so.1 \
    ${base_libdir}/libgcc_s.so \
    ${base_libdir}/libmudflapth.a \
    ${base_libdir}/libitm.a \
    ${base_libdir}/libiberty.a \
    ${base_libdir}/libgomp.a \
    ${base_libdir}/libssp.a \
    ${base_libdir}/libstdc++.so.6.0.19 \
    ${base_libdir}/libsupc++.a \
    ${base_libdir}/libgomp.so \
    ${base_libdir}/libatomic.so.1 \
    ${base_libdir}/libmudflapth.so.0 \
    ${base_libdir}/libmudflap.so \
    ${base_libdir}/libmudflap.a \
    ${base_libdir}/libmudflap.so.0 \
    ${base_libdir}/libssp_nonshared.a \
    ${base_libdir}/libitm.so \
    ${base_libdir}/libitm.so.1 \
    ${base_libdir}/libasan.a \
"

FILES_${PN} = " \
    ${FILES_LIST} \
    ${libdir}/${EXTERNAL_TARGET_SYS}/${BINV}* \
"

INSANE_SKIP_${PN} += "staticdev host-user-contaminated"
