DESCRIPTION = "Generate signed boot files for ADSP platforms"
LICENSE = "CLOSED"
# todo license files

inherit deploy deploy-dep

DDEPENDS = "u-boot-adi adsp-boot"

# overwrite with another signature type in local.conf if you prefer
# valid options are:
# BLp - plaintext with authentication
# BLw - encrypted in wrapped key format
# BLx - encrypted in keyless format
ADI_SIGNATURE_TYPE ?= "BLp"

SIGNTOOL_PROC = "ADSP-SC594"
SIGNTOOL_PROC:adsp-sc598-som-ezkit = "ADSP-SC598"

SIGNTOOL_ALGO = "ecdsa256"

UNSIGNED_SRC = "u-boot-spl-unsigned.ldr u-boot-unsigned.ldr"

do_configure() {
	if [ -z "${ADI_SIGNTOOL_KEY}" ]; then
		bbfatal "Signing key not specified, please set ADI_SIGNTOOL_KEY in local.conf"
	fi

	if [ ! -f "${ADI_SIGNTOOL_PATH}" ]; then
		bbfatal "Must specify a path to the adi_signtool binary as ADI_SIGNTOOL_PATH in local.conf"
	fi

	if [ ! -f "${ADI_SIGNTOOL_KEY}" ]; then
		bbfatal "Signing key '${ADI_SIGNTOOL_KEY}' not found"
	fi
}

do_compile() {
	cd ${WORKDIR}

	for f in ${UNSIGNED_SRC}
	do
		cp ${DEPLOY_DIR_IMAGE}/$f ${WORKDIR}/$f
	done

	${ADI_SIGNTOOL_PATH} -proc ${SIGNTOOL_PROC} sign -type ${ADI_SIGNATURE_TYPE} -algo ${SIGNTOOL_ALGO} \
		-attribute 0x80000002=${LDR_BCODE} \
		-infile u-boot-spl-unsigned.ldr -outfile u-boot-spl.ldr \
		-prikey ${ADI_SIGNTOOL_KEY}

	${ADI_SIGNTOOL_PATH} -proc ${SIGNTOOL_PROC} sign -type ${ADI_SIGNATURE_TYPE} -algo ${SIGNTOOL_ALGO} \
		-attribute 0x80000002=${LDR_BCODE} \
		-infile u-boot-unsigned.ldr -outfile u-boot.ldr \
		-prikey ${ADI_SIGNTOOL_KEY}
}

FILES:${PN} = "adsp-signed-boot.dummy"

do_install() {
	touch ${D}/adsp-signed-boot.dummy
}

do_deploy() {
	install -m 0755 ${WORKDIR}/u-boot-spl.ldr ${DEPLOYDIR}/
	install -m 0755 ${WORKDIR}/u-boot.ldr ${DEPLOYDIR}/
}

addtask do_deploy after do_compile before do_build
