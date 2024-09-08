def get_image_fstype_tasks (d):
    # Take a type in the form of foo.bar.car and split it into the items
    # needed for the image deps "foo", and the conversion deps ["bar", "car"]
    def split_types(typestring):
        types = typestring.split(".")
        return types[0], types[1:]

    # Add do_generate_update_script deps on all image types
    fstypes = set((d.getVar('IMAGE_FSTYPES') or "").split())
    image_fstype_tasks = ""
    for typestring in fstypes:
        basetype, resttypes = split_types(typestring)
        image_fstype_tasks += " do_image_" + basetype
    return image_fstype_tasks

emit_its() {
	cat << EOF > fit-image.its
/dts-v1/;

/ {
	description = "linux-adi FIT image $for ${PV}/${MACHINE}";
	#address-cells = <1>;

	images {
		kernel-1 {
			description = "Linux kernel";
			data = /incbin/("${KERNEL_IMAGETYPE}");
			type = "kernel";
			arch = "${ARCH}";
			os = "linux";
			compression = "${KERNEL_COMPRESSION}";
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

		fdt-2 {
			description = "Flattened Device Tree Blob";
			data = /incbin/("$(basename ${KERNEL_DEVICETREE})");
			type = "flat_dt";
			arch = "${ARCH}";
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

		ramdisk-3 {
			description = "Initial Ram File System";
			data = /incbin/("adsp-sc5xx-ramdisk-${MACHINE}.rootfs.cpio.gz");
			type = "ramdisk";
			arch = "${ARCH}";
			os = "linux";
			compression = "none";
			load = <${UBOOT_RDADDR}>;
			entry = <${UBOOT_RDADDR}>;
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
		default = "conf-1";
		conf-1 {
			description = "boot configuration";
			kernel = "kernel-1";
			fdt = "fdt-2";
			ramdisk = "ramdisk-3";
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
	cd ${DEPLOY_DIR_IMAGE}
        echo "currently in ${DEPLOY_DIR_IMAGE}"
	emit_its;
	uboot-mkimage -D "${UBOOT_MKIMAGE_DTCOPTS}" -f fit-image.its fitImage
}

addtask assemble_fitimage before "${@get_image_fstype_tasks(d)}" after do_rootfs do_image_cpio do_deploy_cpio

do_assemble_fitimage[depends] += "\
    adsp-sc5xx-ramdisk:do_image_complete \
    virtual/bootloader:do_deploy \
    virtual/kernel:do_deploy \
    u-boot-tools-native:do_populate_sysroot \
    u-boot-mkimage-native:do_populate_sysroot \
    dtc-native:do_populate_sysroot \
"

do_assemble_fitimage[vardeps] += " \
    UBOOT_LOADADDRESS \
    UBOOT_ENTRYPOINT \
    UBOOT_SIGN_KEYNAME \
    UBOOT_DTBADDRESS \
    ARCH \
    UBOOT_MKIMAGE_DTCOPTS \
    KERNEL_COMPRESSION \
    KERNEL_DEVICETREE \
    KERNEL_IMAGETYPE \
"

do_image_wic[depends] += "\
     ${PN}:do_assemble_fitimage \
     adsp-sc5xx-ramdisk:do_image_complete \
"

do_image_ext4[depends] += "\
     ${PN}:do_assemble_fitimage \
     adsp-sc5xx-ramdisk:do_image_complete \
"

do_image_jffs2[depends] += "\
     ${PN}:do_assemble_fitimage \
     adsp-sc5xx-ramdisk:do_image_complete \
"
