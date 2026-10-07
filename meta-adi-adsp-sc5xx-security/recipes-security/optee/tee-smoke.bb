SUMMARY = "Linux-visible smoke checks of the TF-A BL31 and OP-TEE stack"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://run-ptest"
S = "${UNPACKDIR}"

inherit ptest features_check

REQUIRED_DISTRO_FEATURES = "optee ptest"
COMPATIBLE_MACHINE = "(adsp-sc598-som-ezkit)"

ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN}-ptest += "optee-client optee-examples rng-tools"
