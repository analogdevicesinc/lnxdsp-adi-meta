DESCRIPTION = "ADSP bootloader payload creation"
LICENSE = "CLOSED"
# todo create uart ldr file again?

DEPENDS = "u-boot-adi ldr-adi-native"

inherit deploy

LDR = "arm-poky-linux-gnueabi-ldr"

# Whatever is LAST in this list will be the jump target from SPL
STAGE_2_SRC = "${DEPLOY_DIR_IMAGE}/u-boot-proper-${BOARD}.elf"
STAGE_2_TARGET_NAME = "stage2-boot.ldr"

FILES_${PN} = " \
	${STAGE_2_TARGET_NAME} \
"

do_compile() {
	${LDR} -T ${LDR_PROC} -c ${B}/${STAGE_2_TARGET_NAME} --bcode=${LDR_BCODE} ${STAGE_2_SRC}
}

do_install () {
	install -m 0755 ${B}/${STAGE_2_TARGET_NAME} ${D}/
}

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 0755 ${B}/${STAGE_2_TARGET_NAME} ${DEPLOYDIR}/
}

addtask deploy after do_install before do_build
