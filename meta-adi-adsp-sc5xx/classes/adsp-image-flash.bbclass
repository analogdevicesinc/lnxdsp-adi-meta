# "flash" image type: flash.img, the whole SPI boot flash, laid out by the
# board's flash.cfg (adsp-genimage.bbclass) as the device tree partitions it.
#
# The rootfs partition holds rootfs.ubi, a UBI image with the EROFS rootfs in
# a static volume named "rootfs", built by the "ubi" conversion as
# <image>.erofs-lz4hc.ubi. U-Boot's spiargs attach UBI to the partition by
# name and mount the volume's ubiblock device, so the root doesn't depend on
# the MTD numbers.

inherit adsp-genimage

# UBI geometry of the SPI NOR boot flashes, which all have 64K erase blocks
ADSP_ROOTFS_UBINIZE_ARGS ?= "-m 1 -p 64KiB -s 1"

# image_types.bbclass, inherited deferred, sets CONVERSIONTYPES with =
CONVERSIONTYPES:append = " ubi"
CONVERSION_DEPENDS_ubi = "mtd-utils-native"
CONVERSION_CMD:ubi = "adsp_ubinize_rootfs ${IMAGE_NAME}.${type}"

adsp_ubinize_rootfs() {
    printf '[rootfs]\nmode=ubi\nimage=%s\nvol_id=0\nvol_type=static\nvol_name=rootfs\n' \
        "$1" > $1.ubinize.cfg
    ubinize -o $1.ubi ${ADSP_ROOTFS_UBINIZE_ARGS} $1.ubinize.cfg
    rm $1.ubinize.cfg
}

IMAGE_TYPES += "flash"
IMAGE_TYPEDEP:flash = "erofs-lz4hc.ubi"

IMAGE_CMD:flash = "adsp_genimage flash"

do_image_flash[depends] += " \
    genimage-native:do_populate_sysroot \
    ${ADSP_GENIMAGE_DEPENDS} \
"
do_image_flash[file-checksums] += "${ADSP_GENIMAGE_DIR}/flash.cfg:True"
