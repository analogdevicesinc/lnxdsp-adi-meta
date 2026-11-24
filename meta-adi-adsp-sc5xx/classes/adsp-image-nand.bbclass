# "nand" image type: a UBI image for the SPI NAND, laid out by the board's
# nand.cfg (adsp-genimage.bbclass), to write with ubiformat.

inherit adsp-genimage

IMAGE_TYPES += "nand"
IMAGE_TYPEDEP:nand = "erofs-lz4hc"

IMAGE_CMD:nand = "adsp_genimage nand"

do_image_nand[depends] += " \
    genimage-native:do_populate_sysroot \
    mtd-utils-native:do_populate_sysroot \
    ${ADSP_GENIMAGE_DEPENDS} \
"
do_image_nand[file-checksums] += "${ADSP_GENIMAGE_DIR}/nand.cfg:True"
