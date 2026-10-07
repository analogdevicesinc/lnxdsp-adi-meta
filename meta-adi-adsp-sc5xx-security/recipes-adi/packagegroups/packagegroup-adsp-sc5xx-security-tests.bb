SUMMARY = "On-target test suites for the TF-A and OP-TEE secure world"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup features_check

REQUIRED_DISTRO_FEATURES = "optee ptest"
COMPATIBLE_MACHINE = "(adsp-sc598-som-ezkit)"

RDEPENDS:${PN} = " \
    optee-test-ptest \
    mbedtls-ptest \
    tee-smoke-ptest \
"
