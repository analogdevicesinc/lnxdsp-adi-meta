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
			data = /incbin/("${FIT_DTB_FILENAME}");
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
			loadables = "ramdisk-3";
			signature-1 {
				algo = "sha1,rsa2048";
				key-name-hint = "${UBOOT_SIGN_KEYNAME}";
			};
		};
	};
};
EOF
}

prepare_fit_dtb() {
	local src_dtb="$(basename ${KERNEL_DEVICETREE})"
	local dst_dtb="${FIT_DTB_FILENAME}"
	local ramdisk_img="adsp-sc5xx-ramdisk-${MACHINE}.rootfs.cpio.gz"
	local ramdisk_size
	local initrd_start
	local initrd_end
	local start_lo
	local end_lo

	cp "$src_dtb" "$dst_dtb"

	ramdisk_size=$(stat -Lc%s "$ramdisk_img")
	initrd_start=$(printf "%d" ${UBOOT_RDADDR})
	initrd_end=$(expr "$initrd_start" + "$ramdisk_size")
	start_lo=$(printf "%x" "$initrd_start")
	end_lo=$(printf "%x" "$initrd_end")

	# Ensure /chosen exists, then encode initrd bounds as 64-bit cells.
	fdtput -c "$dst_dtb" /chosen >/dev/null 2>&1 || true
	fdtput -t x "$dst_dtb" /chosen linux,initrd-start 0 "$start_lo"
	fdtput -t x "$dst_dtb" /chosen linux,initrd-end 0 "$end_lo"
}

do_assemble_fitimage() {
	cd ${DEPLOY_DIR_IMAGE}
        echo "currently in ${DEPLOY_DIR_IMAGE}"
	prepare_fit_dtb;
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
	UBOOT_RDADDR \
    ARCH \
    UBOOT_MKIMAGE_DTCOPTS \
    KERNEL_COMPRESSION \
    KERNEL_DEVICETREE \
    KERNEL_IMAGETYPE \
"

FIT_DTB_FILENAME ?= "falcon-fit.dtb"

do_image_wic[depends] += "\
     ${PN}:do_assemble_fitimage \
     adsp-sc5xx-ramdisk:do_image_complete \
"

do_image_ext4[depends] += "\
     ${PN}:do_assemble_fitimage \
     adsp-sc5xx-ramdisk:do_image_complete \
"

do_image_ubifs[depends] += "\
     ${PN}:do_assemble_fitimage \
     adsp-sc5xx-ramdisk:do_image_complete \
"
