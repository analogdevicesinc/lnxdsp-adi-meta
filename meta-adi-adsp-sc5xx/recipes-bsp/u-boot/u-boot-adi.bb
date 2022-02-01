require u-boot-adi.inc

PR = "r0"

DEPENDS_append_secureboot = "adi-signtool-native u-boot-mkimage-native dtc-native"

SRCREV = "${AUTOREV}"

UBOOT_INITIAL_ENV = ""

EMMC_BOOT_STAGES = ""
EMMC_BOOT_STAGES_adsp-sc598-som-ezkit = "u-boot-${BOARD}.ldr.emmc_boot_stage1 u-boot-${BOARD}.ldr.emmc_boot_stage2"

FILES-SPL = " \
    u-boot-proper-${BOARD}.elf \
    u-boot-spl-${BOARD}.elf \
    u-boot-proper-${BOARD}.ldr \
    u-boot-spl-${BOARD}.ldr \
    u-boot-uart-${BOARD}.ldr \
"

FILES-NO-SPL = " \
    u-boot-${BOARD}.ldr \
    ${EMMC_BOOT_STAGES} \
    u-boot-${BOARD} \
    init-${BOARD}.elf \
"

FILES_${PN} = "${@bb.utils.contains_any('MACHINE_FEATURES', 'spl falcon', '${FILES-SPL}', '${FILES-NO-SPL}', d)}"

FILES_${PN}_append_secureboot = " \
	unsigned-u-boot-spl-${BOARD}.ldr \
	unsigned-u-boot-proper-${BOARD}.ldr \
"

INIT_PATH = ""
INIT_PATH_adsp-sc573-ezkit = "arch/arm/cpu/armv7/sc57x"
INIT_PATH_adsp-sc584-ezkit = "arch/arm/cpu/armv7/sc58x"
INIT_PATH_adsp-sc589-ezkit = "arch/arm/cpu/armv7/sc58x"
INIT_PATH_adsp-sc589-mini = "arch/arm/cpu/armv7/sc58x"
INIT_PATH_adsp-sc594-som-ezkit = "arch/arm/cpu/armv7/sc59x"
INIT_PATH_adsp-sc598-som-ezkit = "arch/arm/cpu/armv8/sc59x-64"

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
		dummy@1 {
			description = "dummy";
			data = [00];
			type = "kernel";
			arch = "arm";
			os = "linux";
			compression = "none";
			load = <0x80008000>;
			entry = <0x80008000>;
			hash@1 {
				algo = "sha1";
			};
		};
	};
	configurations {
		default = "conf@1";
		conf@1 {
			description = "dummy";
			dummy = "dummy@1";
			hash@1 {
					algo = "sha1";
			};
			signature@1 {
				algo = "sha1,rsa2048";
				key-name-hint = "${UBOOT_SIGN_KEYNAME}";
				sign-images = "dummy";
			};
		};
	};
};
EOF
}

do_configure_prepend() {
	if [ -n "${BOARD_HAS_SPL}" ]; then
		bbwarn "You should consider using SPL or falcon boot instead of legacy boot"
	fi
}

do_configure_prepend_secureboot() {
	if [ ! -d "${UBOOT_SIGN_KEYDIR}" ]; then
		bbfatal "Missing or invalid UBOOT_SIGN_KEYDIR (= '${UBOOT_SIGN_KEYDIR}')"
	fi
	if [ ! -f  "${UBOOT_SIGN_KEYDIR}/${UBOOT_SIGN_KEYNAME}.key" ]; then
		bbfatal "Missing key matching ${UBOOT_SIGN_KEYNAME}, looking for '${UBOOT_SIGN_KEYDIR}/${UBOOT_SIGN_KEYNAME}.key'"
	fi
}

# For secure boot, rewrite the compile step to build dtbs separately and inject the signing key
# and then call adi signtool on the resulting uboot images
do_compile_secureboot() {
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'ld-is-gold', d)}" ]; then
		sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' ${S}/config.mk
	fi

	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	if [ ! -e ${B}/.scmversion -a ! -e ${S}/.scmversion ]
	then
		echo ${UBOOT_LOCALVERSION} > ${B}/.scmversion
		echo ${UBOOT_LOCALVERSION} > ${S}/.scmversion
	fi

	if [ -n "${UBOOT_CONFIG}" -o -n "${UBOOT_DELTA_CONFIG}" ]
	then
		unset i j k
		for config in ${UBOOT_MACHINE}; do
			i=$(expr $i + 1);
			for type in ${UBOOT_CONFIG}; do
				j=$(expr $j + 1);
				if [ $j -eq $i ]
				then
					oe_runmake -C ${S} O=${B}/${config} ${config}
					oe_runmake -C ${S} O=${B}/${config} dtbs
					sits_emit

					uboot-mkimage -D "${UBOOT_MKIMAGE_DTCOPTS}" \
						-f ${WORKDIR}/simple.its ${B}/simpleFitImage

					uboot-mkimage -D "${UBOOT_MKIMAGE_DTCOPTS}" \
						-F -k ${UBOOT_SIGN_KEYDIR} \
						-K ${B}/${config}/dts/dt.dtb \
						-r ${B}/simpleFitImage

					rm ${WORKDIR}/simple.its
					rm ${B}/simpleFitImage

					oe_runmake -C ${S} O=${B}/${config} ${UBOOT_MAKE_TARGET}
					for binary in ${UBOOT_BINARIES}; do
						k=$(expr $k + 1);
						if [ $k -eq $i ]; then
							cp ${B}/${config}/${binary} ${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX}
						fi
					done

					# Generate the uboot-initial-env
					if [ -n "${UBOOT_INITIAL_ENV}" ]; then
						oe_runmake -C ${S} O=${B}/${config} u-boot-initial-env
						cp ${B}/${config}/u-boot-initial-env ${B}/${config}/u-boot-initial-env-${type}
					fi

					unset k
				fi
			done
			unset  j
		done
		unset  i
	else
		oe_runmake -C ${S} O=${B} ${UBOOT_MACHINE}
		oe_runmake -C ${S} O=${B} dtbs

		sits_emit

		uboot-mkimage -D "${UBOOT_MKIMAGE_DTCOPTS}" \
			-f ${WORKDIR}/simple.its ${B}/simpleFitImage

		uboot-mkimage -D "${UBOOT_MKIMAGE_DTCOPTS}" \
			-F -k ${UBOOT_SIGN_KEYDIR} \
			-K ${B}/dts/dt.dtb \
			-r ${B}/simpleFitImage

		rm ${WORKDIR}/simple.its
		rm ${B}/simpleFitImage

		oe_runmake -C ${S} O=${B} ${UBOOT_MAKE_TARGET}

		# Generate the uboot-initial-env
		if [ -n "${UBOOT_INITIAL_ENV}" ]; then
			oe_runmake -C ${S} O=${B} u-boot-initial-env
		fi
	fi

	if [ -z "${ADI_SIGNTOOL_KEY}" ]; then
		bbfatal "Signing key not specified, please set ADI_SIGNTOOL_KEY in local.conf"
	fi

	if [ ! -f "${ADI_SIGNTOOL_PATH}" ]; then
		bbfatal "Must specify a path to the adi_signtool binary as ADI_SIGNTOOL_PATH in local.conf"
	fi

	if [ ! -f "${ADI_SIGNTOOL_KEY}" ]; then
		bbfatal "Signing key '${ADI_SIGNTOOL_KEY}' not found"
	fi

	# Until adi_signtool has absolute paths fixed, things need to be done in deploydir
	pushd ${B}

	${ADI_SIGNTOOL_PATH} -proc ${SIGNTOOL_PROC} sign -type ${ADI_SIGNATURE_TYPE} -algo ecdsa256 \
		-infile u-boot-spl-${BOARD}.ldr -outfile u-boot-spl-signed-${BOARD}.ldr \
		-prikey ${ADI_SIGNTOOL_KEY}

	${ADI_SIGNTOOL_PATH} -proc ${SIGNTOOL_PROC} sign -type ${ADI_SIGNATURE_TYPE} -algo ecdsa256 \
		-infile u-boot-proper-${BOARD}.ldr -outfile u-boot-proper-signed-${BOARD}.ldr \
		-prikey ${ADI_SIGNTOOL_KEY}

	popd
}

do_install () {
    if [ -z "${BASH_HAS_SPL}" ]; then
        install ${B}/u-boot-${BOARD}.ldr ${D}/
        if [ "${MACHINE}" = "adsp-sc598-som-ezkit" ]; then
            install ${B}/u-boot-${BOARD}.ldr.emmc_boot_stage1 ${D}/
            install ${B}/u-boot-${BOARD}.ldr.emmc_boot_stage2 ${D}/
        fi
        install ${B}/u-boot-${BOARD} ${D}/
        install ${B}/${INIT_PATH}/init-${BOARD}.elf ${D}/
    else
        install ${B}/u-boot-proper-${BOARD}.elf ${D}/
        install ${B}/u-boot-spl-${BOARD}.elf ${D}/
        install ${B}/u-boot-proper-${BOARD}.ldr ${D}/
        install ${B}/u-boot-spl-${BOARD}.ldr ${D}/
        install ${B}/u-boot-uart-${BOARD}.ldr ${D}/
    fi
}

# Install signed versions in place of spl and proper, add unsigned copies
do_install_secureboot() {
	if [ -z "${BASH_HAS_SPL}"]; then
		bbfatal "For secure boot, you must use an SPL build"
	fi

	install ${B}/u-boot-spl-signed-${BOARD}.ldr ${D}/u-boot-spl-${BOARD}.ldr
	install ${B}/u-boot-proper-signed-${BOARD}.ldr ${D}/u-boot-proper-${BOARD}.ldr
	install ${B}/u-boot-spl-${BOARD}.ldr ${D}/unsigned-u-boot-spl-${BOARD}.ldr
	install ${B}/u-boot-proper-${BOARD}.ldr ${D}/unsigned-u-boot-proper-${BOARD}.ldr
	install ${B}/u-boot-proper-${BOARD}.elf ${D}/
	install ${B}/u-boot-spl-${BOARD}.elf ${D}/
	install ${B}/u-boot-uart-${BOARD}.ldr ${D}/
}

do_deploy() {
    if [ -z "${BASH_HAS_SPL}" ]; then
        install ${B}/u-boot-${BOARD}.ldr ${DEPLOYDIR}/
        if [ "${MACHINE}" = "adsp-sc598-som-ezkit" ]; then
            install ${B}/u-boot-${BOARD}.ldr.emmc_boot_stage1 ${DEPLOYDIR}/
            install ${B}/u-boot-${BOARD}.ldr.emmc_boot_stage2 ${DEPLOYDIR}/
        fi
        install ${B}/u-boot-${BOARD} ${DEPLOYDIR}/
        install ${B}/${INIT_PATH}/init-${BOARD}.elf ${DEPLOYDIR}/
    else
        install ${B}/u-boot-proper-${BOARD}.elf ${DEPLOYDIR}/
        install ${B}/u-boot-spl-${BOARD}.elf ${DEPLOYDIR}/
        install ${B}/u-boot-proper-${BOARD}.ldr ${DEPLOYDIR}/
        install ${B}/u-boot-spl-${BOARD}.ldr ${DEPLOYDIR}/
        install ${B}/u-boot-uart-${BOARD}.ldr ${DEPLOYDIR}/
    fi
}

do_deploy_secureboot() {
	install ${B}/u-boot-spl-signed-${BOARD}.ldr ${DEPLOYDIR}/u-boot-spl-${BOARD}.ldr
	install ${B}/u-boot-proper-signed-${BOARD}.ldr ${DEPLOYDIR}/u-boot-proper-${BOARD}.ldr
	install ${B}/u-boot-spl-${BOARD}.ldr ${DEPLOYDIR}/unsigned-u-boot-spl-${BOARD}.ldr
	install ${B}/u-boot-proper-${BOARD}.ldr ${DEPLOYDIR}/unsigned-u-boot-proper-${BOARD}.ldr
	install ${B}/u-boot-uart-${BOARD}.ldr ${DEPLOYDIR}/
	install ${B}/u-boot-proper-${BOARD}.elf ${DEPLOYDIR}/
	install ${B}/u-boot-spl-${BOARD}.elf ${DEPLOYDIR}/
}
