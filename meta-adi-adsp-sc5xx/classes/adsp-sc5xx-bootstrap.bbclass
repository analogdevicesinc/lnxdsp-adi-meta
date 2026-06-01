
inherit adsp-sc5xx-ramdisk

BOOTSTRAP_SOURCE_IMAGE ?= "adsp-sc5xx-minimal"

PACKAGE_INSTALL:remove = "initramfs-init"
PACKAGE_INSTALL:append = " \
	initramfs-init-programming \
    mtd-utils \
    mtd-utils-ubifs \
    e2fsprogs \
    util-linux \
"

fakeroot do_install_bootstrap_firmware() {
    PROG_DIR="${DEPLOY_DIR_IMAGE}/programming-images/${BOOTSTRAP_SOURCE_IMAGE}"

    if [ ! -d "${PROG_DIR}" ]; then
        bbfatal "Bootstrap firmware not found in ${PROG_DIR}."
    fi

    install -d "${IMAGE_ROOTFS}/usr/firmware"

	for fw in u-boot-spl.ldr u-boot.ldr fitImage rootfs.ubi; do
		if [ ! -f "${PROG_DIR}/${fw}" ]; then
			bbfatal "Required bootstrap firmware image missing: ${PROG_DIR}/${fw}"
		fi
		install -m 0644 "${PROG_DIR}/${fw}" "${IMAGE_ROOTFS}/usr/firmware/${fw}"
	done
}

addtask install_bootstrap_firmware after do_rootfs_cleanup before do_image

do_install_bootstrap_firmware[depends] += "${BOOTSTRAP_SOURCE_IMAGE}:do_create_programming_images"
do_install_bootstrap_firmware[vardeps] += "BOOTSTRAP_SOURCE_IMAGE"

emit_bootstrap_its() {
    cat << EOF > bootstrap-fit-image.its
/dts-v1/;

/ {
	description = "ADSP-SC5xx Bootstrap FIT Image for ${MACHINE}";
	#address-cells = <1>;

	images {
		kernel-1 {
			description = "Linux kernel";
			data = /incbin/("${KERNEL_IMAGETYPE}");
			type = "kernel";
			arch = "${ARCH}";
			os = "linux";
			compression = "${KERNEL_COMPRESSION}";
			load = <${BOOTSTRAP_LOADADDR}>;
			entry = <${BOOTSTRAP_LOADADDR}>;
			hash-1 {
				algo = "sha1";
			};
		};

		fdt-2 {
			description = "Flattened Device Tree Blob";
			data = /incbin/("${BOOTSTRAP_DTB}");
			type = "flat_dt";
			arch = "${ARCH}";
			compression = "none";
			hash-1 {
				algo = "sha1";
			};
		};

		ramdisk-3 {
			description = "Bootstrap initramfs";
			data = /incbin/("${PN}-${MACHINE}.rootfs.cpio.gz");
			type = "ramdisk";
			arch = "${ARCH}";
			os = "linux";
			compression = "none";
			hash-1 {
				algo = "sha1";
			};
		};
	};

	configurations {
		default = "conf-1";
		conf-1 {
			description = "Bootstrap boot configuration";
			kernel = "kernel-1";
			fdt = "fdt-2";
			ramdisk = "ramdisk-3";
		};
	};
};
EOF
}

do_assemble_bootstrap_fitimage() {
    cd "${DEPLOY_DIR_IMAGE}"
    echo "Assembling bootstrap FIT image in ${DEPLOY_DIR_IMAGE}"

	BOOTSTRAP_DTB="bootstrap-$(basename ${KERNEL_DEVICETREE})"
	cp "$(basename ${KERNEL_DEVICETREE})" "${BOOTSTRAP_DTB}"
	fdtput -t s "${BOOTSTRAP_DTB}" /chosen bootargs "console=ttySC0,115200 root=/dev/ram0 rw rdinit=/init"

    emit_bootstrap_its

    uboot-mkimage -D "${UBOOT_MKIMAGE_DTCOPTS}" \
        -f bootstrap-fit-image.its \
        "bootstrap-fitImage"
}
addtask assemble_bootstrap_fitimage after do_image_complete before do_build

do_assemble_bootstrap_fitimage[depends] += " \
    virtual/bootloader:do_deploy \
    virtual/kernel:do_deploy \
    u-boot-tools-native:do_populate_sysroot \
    u-boot-mkimage-native:do_populate_sysroot \
    dtc-native:do_populate_sysroot \
"

do_assemble_bootstrap_fitimage[vardeps] += " \
    UBOOT_LOADADDRESS \
    UBOOT_ENTRYPOINT \
    UBOOT_DTBADDRESS \
	BOOTSTRAP_LOADADDR \
    ARCH \
    UBOOT_MKIMAGE_DTCOPTS \
    KERNEL_COMPRESSION \
    KERNEL_DEVICETREE \
    KERNEL_IMAGETYPE \
    BOOTSTRAP_SOURCE_IMAGE \
"
