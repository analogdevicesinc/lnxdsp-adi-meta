DESCRIPTION = "ADSP bootloader payload creation"
LICENSE = "CLOSED"
# todo create uart ldr file again?

DEPENDS = "u-boot-adi ldr-adi-native"
DDEPENDS = "u-boot-adi"

LDR = "ldr"

# Whatever is LAST in this list will be the jump target from SPL
STAGE_2_SRC = "u-boot-proper-${BOARD}.elf"
STAGE_2_TARGET_NAME = "stage2-boot.ldr"

inherit deploy deploy-dep

do_compile() {
	for f in ${STAGE_2_SRC}
	do
		cp ${DEPLOY_DIR_IMAGE}/$f ${WORKDIR}/$f
	done

	cd ${WORKDIR}
	${LDR} -T ${LDR_PROC} -c ${B}/${STAGE_2_TARGET_NAME} --bcode=${LDR_BCODE} ${STAGE_2_SRC}
}

FILES:${PN} = "adsp-boot.dummy"

do_install() {
	touch ${D}/adsp-boot.dummy
}

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 0755 ${B}/${STAGE_2_TARGET_NAME} ${DEPLOYDIR}/
}

addtask deploy after do_compile before do_install
