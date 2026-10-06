
# If building optee support, enable it and include it in stage 2 image
DDEPENDS:append:adsp-sc598-som-ezkit = " ${@bb.utils.contains('DISTRO_FEATURES','optee','trusted-firmware-a optee-os-elf','',d)}"
# todo: when building without uboot support include kernel "elf" version
#DDEPENDS:append:adsp-sc598-som-ezkit = " linux-adi-elf"

# With TF-A, stage 2 is OP-TEE + BL31 only: the SPL Falcon-loads the kernel and BL31 enters it
STAGE_2_SRC:adsp-sc598-som-ezkit = "${@bb.utils.contains('DISTRO_FEATURES','optee','tee.elf bl31.elf','u-boot-proper-${BOARD}.elf',d)}"

# This needs to be last to jump to it
STAGE_2_SRC:append:optee-shim = " optee-shim.elf"

# If we'll be signing the output later, call it unsigned for the signing recipe
# to be able to find it
STAGE_2_TARGET_NAME:adsp-sc5xx-signedboot = "u-boot-unsigned"

# JTAG (bmode 0) bootstrap ELF: U-Boot proper + stage 2, entered at the last
# stage 2 image like the LDR; BL31 then runs U-Boot proper as BL33
UBOOT_ELF_SRC = "u-boot-proper-${BOARD}.elf ${STAGE_2_SRC}"

do_compile:append:adsp-sc598-som-ezkit() {
	if ${@bb.utils.contains('DISTRO_FEATURES','optee','true','false',d)}; then
		sections=""
		objs=""
		for f in ${UBOOT_ELF_SRC}; do
			cp ${DEPLOY_DIR_IMAGE}/$f ${WORKDIR}/$f
			n=$(basename $f .elf | tr -c 'a-zA-Z0-9\n' '_')
			addr=$(${READELF} -lW $f | awk '$1 == "LOAD" { print $4; exit }')
			entry=$(${READELF} -hW $f | awk '/Entry point/ { print $4 }')
			${OBJCOPY} -O binary $f $n.bin
			${OBJCOPY} -I binary -O elf64-littleaarch64 -B aarch64 \
				--rename-section .data=.$n,alloc,load,contents,code $n.bin $n.o
			sections="$sections --section-start=.$n=$addr"
			objs="$objs $n.o"
		done
		${LD} -N --no-warn-rwx-segments -e $entry $sections -o ${B}/u-boot.elf $objs
	fi
}

do_deploy:append:adsp-sc598-som-ezkit() {
	if [ -f ${B}/u-boot.elf ]; then
		install -m 0755 ${B}/u-boot.elf ${DEPLOYDIR}/
	fi
}
