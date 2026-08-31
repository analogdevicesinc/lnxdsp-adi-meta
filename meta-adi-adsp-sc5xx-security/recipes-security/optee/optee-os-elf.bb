SUMMARY = "OP-TEE OS as an ELF"
DESCRIPTION = "Package OP-TEE OS back into an functional ELF so that it can be used with tools that expect ELF objects"
LICENSE = "CLOSED"

DDEPENDS = "optee-os"

SRC_URI = " \
	file://optee-elf.ld.in \
"

# The shim is used to work around a limitation in the LDR building. See optee-v7-shim.S
# for details as to why you might need it (the platforms not running TF-A need it)
SRC_URI:append:optee-shim = " \
	file://optee-v7-shim.ld \
	file://optee-v7-shim.S \
"

OUTPUT_FORMAT = "elf64-littleaarch64"
OUTPUT_FORMAT:armv7a := "elf32-littlearm"

OUTPUT_ARCH = "aarch64"
OUTPUT_ARCH:armv7a = "arm"

inherit deploy deploy-dep

addtask deploy after do_compile before do_build

do_compile() {
	cp ${DEPLOY_DIR_IMAGE}/tee.bin ${WORKDIR}/tee.bin

	${OBJCOPY} -I binary ${WORKDIR}/tee.bin -O ${OUTPUT_FORMAT} ${B}/tee.o

	sed -e "s/LOAD_ADDRESS/${OPTEE_LOAD_ADDRESS}/" \
		-e "s/REPLACE_OUTPUT_FORMAT/${OUTPUT_FORMAT}/" \
		-e "s/REPLACE_OUTPUT_ARCH/${OUTPUT_ARCH}/" \
		${WORKDIR}/optee-elf.ld.in > ${WORKDIR}/optee-elf.ld

	${CC} -nostartfiles -nostdlib -static -T ${WORKDIR}/optee-elf.ld ${B}/tee.o -o ${B}/tee.elf -Wl,--nmagic
}

do_compile:append:optee-shim() {
	${CC} -DOPTEE_START_ADDRESS=${OPTEE_START_ADDRESS} \
		-nostartfiles -nostdlib -static -T ${WORKDIR}/optee-v7-shim.ld -Wl,--nmagic \
		${WORKDIR}/optee-v7-shim.S -o ${B}/optee-shim.elf
}

do_install[noexec] = "1"

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 0755 ${B}/tee.elf ${DEPLOYDIR}/tee.elf
}

do_deploy:append:optee-shim() {
	install -m 0755 ${B}/optee-shim.elf ${DEPLOYDIR}/optee-shim.elf
}
