inherit core-image extrausers adsp-sc5xx-compatible

SUMMARY = "eMMC/MMC provisioning ramdisk image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

#For transferring data to flash eMMC/SD via ramdisk
MMC_UTILS = " \
    openssh \
    e2fsprogs-resize2fs \
    gzip \
    adi-flash-emmc \
    mtd-utils \
    mtd-utils-ubifs \
"

IMAGE_INSTALL:append = " \
    ${MMC_UTILS} \
"
