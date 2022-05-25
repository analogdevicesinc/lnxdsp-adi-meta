require linux-adi.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

DEPENDS += "u-boot-mkimage-native dtc-native"

PR = "r0"

PV = "5.4.183"

KERNEL_BRANCH ?= "release/2.1.0"
SRCREV  = "1fb73edfe59cfeed90103a3a5caf486199fbc63f"

SRC_URI += "file://feature/"

# Include kernel configuration fragment
KERNEL_EXTRA_FEATURES ?= "${WORKDIR}/feature/cfg/nfs.cfg \
						  ${WORKDIR}/feature/cfg/wireless.cfg \
						  ${WORKDIR}/feature/cfg/cpufreq.cfg \
						  ${WORKDIR}/feature/cfg/crypto.cfg \
						  "
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"

KERNEL_FEATURES_append_adsp-sc594_som_ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc589-ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc584-ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc573-ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc589-mini = " ${WORKDIR}/feature/snd_mini.scc"

do_install_append(){
	rm -rf ${D}/lib/modules/5.4.0-yocto-standard/modules.builtin.modinfo
}

# @todo this doesn't support ramdisk booting yet
emit_its() {
	cat << EOF > ${B}/fit-image.its
/dts-v1/;

/ {
	description = "linux-adi FIT image $for ${PV}/${MACHINE}";
	#address-cells = <1>;

	images {
		kernel@1 {
			description = "Linux kernel";
			data = /incbin/("arch/arm64/boot/Image");
			type = "kernel";
			arch = "arm64";
			os = "linux";
			compression = "none";
			load = <${UBOOT_LOADADDRESS}>;
			entry = <${UBOOT_ENTRYPOINT}>;
			hash-1 {
				algo = "sha1";
			};
			signature-1 {
				algo = "sha1,rsa2048";
				key-name-hint = "${UBOOT_SIGN_KEYNAME}";
			};
		};

		fdt@2 {
			description = "Flattened Device Tree Blob";
			data = /incbin/("arch/arm64/boot/dts/adi/sc598-som-ezkit.dtb");
			type = "flat_dt";
			arch = "arm64";
			compression = "none";
			load = <${UBOOT_DTBADDRESS}>;
			hash-1 {
				algo = "sha1";
			};
			signature-1 {
				algo = "sha1,rsa2048";
				key-name-hint = "${UBOOT_SIGN_KEYNAME}";
			};
		};
	};

	configurations {
		default = "conf@1";
		conf@1 {
			description = "boot configuration";
			kernel = "kernel@1";
			fdt = "fdt@2";
			signature-1 {
				algo = "sha1,rsa2048";
				key-name-hint = "${UBOOT_SIGN_KEYNAME}";
			};
		};
	};
};
EOF
}

do_assemble_fitimage() {
	cd ${B}

	emit_its;

	uboot-mkimage -D "${UBOOT_MKIMAGE_DTCOPTS}" -f fit-image.its \
		arch/arm64/boot/fitImage
}

addtask assemble_fitimage before do_install after do_compile

KERNEL_IMAGETYPES_append = " fitImage"
