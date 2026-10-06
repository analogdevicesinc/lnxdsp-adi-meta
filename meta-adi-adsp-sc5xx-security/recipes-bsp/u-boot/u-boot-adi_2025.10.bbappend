
DEPENDS:append:adsp-sc5xx-signedboot = " u-boot-mkimage-native dtc-native"

STAGE_1_TARGET_NAME:adsp-sc5xx-signedboot = "u-boot-spl-unsigned"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:adsp-sc598-som-ezkit = "${@bb.utils.contains('DISTRO_FEATURES', 'signedboot', ' file://0001-sc598-uboot-partitions-update-command.patch file://0002-arm-mach-sc5xx-Start-TF-A-from-the-SPL-for-secure-boot.patch file://spl-secure-falcon.cfg', '', d)}"

STAGE_1_TFA_ELF_ADDR = "0x20100000"

do_compile[depends] += "${@'trusted-firmware-a:do_deploy optee-os-elf:do_deploy' if d.getVar('SECURE_BOOT') else ''}"

do_compile:append() {
	if [ -n "${SECURE_BOOT}" ]; then
		cd ${B}
		sections=""
		objs=""
		for f in tee.elf bl31.elf; do
			n=$(basename $f .elf)
			addr=$(${READELF} -lW ${DEPLOY_DIR_IMAGE}/$f | awk '$1 == "LOAD" { print $4; exit }')
			entry=$(${READELF} -hW ${DEPLOY_DIR_IMAGE}/$f | awk '/Entry point/ { print $4 }')
			${OBJCOPY} -O binary ${DEPLOY_DIR_IMAGE}/$f $n.bin
			${OBJCOPY} -I binary -O elf64-littleaarch64 -B aarch64 \
				--rename-section .data=.$n,alloc,load,contents,code $n.bin $n.o
			sections="$sections --section-start=.$n=$addr"
			objs="$objs $n.o"
		done
		${LD} -N --no-warn-rwx-segments -e $entry $sections -o tfa.elf $objs

		${OBJCOPY} -I binary -O elf64-littleaarch64 -B aarch64 \
			--rename-section .data=.tfa_stage,alloc,load,contents tfa.elf tfa-stage.o
		${LD} -N --no-warn-rwx-segments -e ${STAGE_1_TFA_ELF_ADDR} \
			--section-start=.tfa_stage=${STAGE_1_TFA_ELF_ADDR} -o tfa-stage.elf tfa-stage.o

		ldr -T $(sed -n 's/^CONFIG_LDR_CPU="\(.*\)"/\1/p' .config) -c spl/u-boot-spl.ldr \
			--bcode=$(sed -n 's/^CONFIG_SC_BCODE=//p' .config) --use-vmas \
			tfa-stage.elf spl/u-boot-spl

		# U-Boot proper as a FIT the SPL verifies with the same key as the kernel FIT
		text_base=$(sed -n 's/^CONFIG_TEXT_BASE=//p' .config)
		cat > u-boot.its <<EOF
/dts-v1/;

/ {
	description = "U-Boot proper";
	#address-cells = <1>;

	images {
		uboot {
			data = /incbin/("u-boot.bin");
			type = "firmware";
			os = "u-boot";
			arch = "arm64";
			compression = "none";
			load = <$text_base>;
			entry = <$text_base>;
			hash {
				algo = "sha256";
			};
		};
	};

	configurations {
		default = "conf-1";
		conf-1 {
			description = "U-Boot proper";
			firmware = "uboot";
			signature {
				algo = "sha256,rsa2048";
				key-name-hint = "${UBOOT_SIGN_KEYNAME}";
				sign-images = "firmware";
			};
		};
	};
};
EOF
		uboot-mkimage -D "${UBOOT_MKIMAGE_DTCOPTS}" -f u-boot.its \
			-k ${UBOOT_SIGN_KEYDIR} u-boot.itb
	fi
}

do_deploy:append() {
	if [ -n "${SECURE_BOOT}" ]; then
		install -m 0644 ${B}/u-boot.itb ${DEPLOYDIR}/u-boot
	fi
}

# Actual contents of this don't matter, we just need to sign this fit image in order to get uboot
# to update the dtb with the key that was used for signing, which will be used to sign the kernel
# fit image later
sits_emit() {
	cat << EOF > ${WORKDIR}/simple.its
/dts-v1/;

/ {
	description = "U-Boot Simple fitImage";
	#address-cells = <1>;
	images {
		dummy-img {
			description = "dummy";
			data = [00];
			type = "kernel";
			arch = "arm";
			os = "linux";
			compression = "none";
			load = <0x80008000>;
			entry = <0x80008000>;
			hash {
				algo = "sha256";
			};
		};
	};
	configurations {
		default = "conf-1";
		conf-1 {
			description = "dummy";
			dummy = "dummy-img";
			hash {
					algo = "sha256";
			};
			signature {
				algo = "sha256,rsa2048";
				key-name-hint = "${UBOOT_SIGN_KEYNAME}";
				sign-images = "dummy";
			};
		};
	};
};
EOF
}

do_configure:prepend:adsp-sc5xx-signedboot() {
	if [ ! -d "${UBOOT_SIGN_KEYDIR}" ]; then
		bbfatal "Missing or invalid UBOOT_SIGN_KEYDIR (= '${UBOOT_SIGN_KEYDIR}')"
	fi
	if [ ! -f  "${UBOOT_SIGN_KEYDIR}/${UBOOT_SIGN_KEYNAME}.key" ]; then
		bbfatal "Missing key matching ${UBOOT_SIGN_KEYNAME}, looking for '${UBOOT_SIGN_KEYDIR}/${UBOOT_SIGN_KEYNAME}.key'"
	fi
	if [ -z "${BASH_HAS_SPL}" ]; then
		bbfatal "For signed boot, you must use an SPL build"
	fi
}

# Add/Inject FIT public key into U-Boot DTS prior to U-Boot compilation
do_compile:prepend:adsp-sc5xx-signedboot(){
	sits_emit

	DTS_NAME=$(echo "${MACHINE}" | sed 's/^adsp-//')

	INCLUDE=${S}/arch/arm/dts/
	INCLUDE2=${S}/include
	INCLUDE3=${S}/arch/arm/include/asm
	INCLUDE4=${S}/dts/upstream/include
	SRC=${S}/arch/arm/dts/${DTS_NAME}.dts
	TMP=${WORKDIR}/${DTS_NAME}.dts.tmp

	cpp -nostdinc -I${INCLUDE} -I${INCLUDE2} -I${INCLUDE3} -I${INCLUDE4} -undef -x assembler-with-cpp ${SRC} > ${TMP}

	dtc ${UBOOT_MKIMAGE_DTCOPTS} \
	-o ${WORKDIR}/${DTS_NAME}.dtb ${TMP}

	uboot-mkimage -D "${UBOOT_MKIMAGE_DTCOPTS}" \
	-f ${WORKDIR}/simple.its ${WORKDIR}/simpleFitImage

	uboot-mkimage -D "${UBOOT_MKIMAGE_DTCOPTS}" \
	-F -k ${UBOOT_SIGN_KEYDIR} \
	-K ${WORKDIR}/${DTS_NAME}.dtb \
	-r ${WORKDIR}/simpleFitImage

	dtc ${UBOOT_MKIMAGE_DTCOPTS} -I dtb -O dts \
	-o ${SRC} ${WORKDIR}/${DTS_NAME}.dtb

	#Allow key to persist in SPL DTB as well, via u-boot,dm-pre-reloc flag.
	LINE=$(sed -n '/key-name-hint/=' ${SRC})        #Find the end of the key node in DTS
	LINE=$(expr ${LINE} + 1)                        #Increment to the next line
	MATCH=$(sed -e ${LINE}'!d' -e 's/\t//g' ${SRC}) #Grab the next line

	#Check if the flag already exists, add it if not
	if [ "${MATCH}" != "u-boot,dm-pre-reloc;" ]; then
		sed -i 's/.*key-name-hint.*/&\n\t\t\tu-boot,dm-pre-reloc;/' ${SRC}
	fi
}
