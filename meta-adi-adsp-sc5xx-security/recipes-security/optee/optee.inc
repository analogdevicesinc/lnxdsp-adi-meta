# Set here but not passed to EXTRA_OEMAKE by default as that breaks
# the optee-os build
TA_DEV_KIT_DIR = "${STAGING_INCDIR}/optee/export-user_ta"

OPTEE_TA_CC_PREFIX ?= "CROSS_COMPILE_ta_arm64"
OPTEE_TA_CC_PREFIX:armv7a := "CROSS_COMPILE_ta_arm32"

EXTRA_OEMAKE += "V=1 \
                 OPTEE_CLIENT_EXPORT=${STAGING_DIR_HOST}${prefix} \
                 CROSS_COMPILE_HOST=${HOST_PREFIX} \
                 CROSS_COMPILE_TA=${HOST_PREFIX} \
                 ${OPTEE_TA_CC_PREFIX}=${HOST_PREFIX} \
                 TEEC_EXPORT=${STAGING_DIR_HOST}${prefix} \
                "

kludge_openssl() {
	# see: poky/openssl_3.x.bb do_install:append:class-native for how the openssl
	# wrapper is supposed to be generated, which doesn't seem to propagate to
	# python3-cryptography-native modules properly
	export OPENSSL_CONF="${STAGING_LIBDIR_NATIVE}/ssl-3/openssl.cnf"
	export OPENSSL_ENGINES="${STAGING_LIBDIR_NATIVE}/engines-3"
	export OPENSSL_MODULES="${STAGING_LIBDIR_NATIVE}/ossl-modules"
	export SSL_CERT_DIR="${STAGING_LIBDIR_NATIVE}/ssl-3/certs"
}
