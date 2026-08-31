SUMMARY = "OP-TEE examples"
DESCRIPTION = "Open Portable Trusted Execution Environment - Sample Applications"
HOMEPAGE = "https://github.com/linaro-swg/optee_examples"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cd95ab417e23b94f381dafc453d70c30"

DEPENDS = "optee-client optee-os python3-cryptography-native"

inherit python3native

require optee.inc

SRC_URI = " \
    git://github.com/linaro-swg/optee_examples.git;branch=master;protocol=https \
"

EXTRA_OEMAKE += " \
    TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
    HOST_CROSS_COMPILE=${HOST_PREFIX} \
    TA_CROSS_COMPILE=${HOST_PREFIX} \
    OUTPUT_DIR=${B} \
"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

do_compile() {
    export CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_HOST}"
    kludge_openssl
    oe_runmake -C ${S}
}
do_compile[cleandirs] = "${B}"

do_install () {
    install -d ${D}${nonarch_base_libdir}/optee_armtz
    install -d ${D}${bindir}
    install -d ${D}${libdir}/tee-supplicant/plugins
    install -m 0755 ${B}/ca/* ${D}${bindir}
    install -m 0444 ${B}/ta/* ${D}${nonarch_base_libdir}/optee_armtz
    install -m 0444 ${B}/plugins/* ${D}${libdir}/tee-supplicant/plugins
}

FILES:${PN} += " \
    ${nonarch_base_libdir}/optee_armtz/ \
    ${libdir}/tee-supplicant/plugins/ \
"

# Imports machine specific configs from staging to build
PACKAGE_ARCH = "${MACHINE_ARCH}"
