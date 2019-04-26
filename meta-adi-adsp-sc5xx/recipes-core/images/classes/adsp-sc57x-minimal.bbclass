inherit core-image

SUMMARY = "Minimal image for Analog Devices ADSP-SC57x boards"
LICENSE = "MIT"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-base \
    ${CORE_IMAGE_EXTRA_INSTALL} \
"

COMPATIBLE_MACHINE = "(adsp-sc573-ezkit)"

#BR2_PACKAGE_BUSYBOX_CONFIG="board/AnalogDevices/arm/busybox.config"
#BR2_PACKAGE_BUSYBOX_WATCHDOG=y
#BR2_PACKAGE_OPENSSL_BIN=y
#BR2_PACKAGE_LIBTIRPC=y
#BR2_PACKAGE_OPENSSH=y
#BR2_TARGET_ROOTFS_EXT2=y
#BR2_TARGET_ROOTFS_EXT2_3=y
#BR2_TARGET_ROOTFS_INITRAMFS=y




