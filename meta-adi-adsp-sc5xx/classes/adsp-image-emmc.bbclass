# "emmc" image type: an image of the whole eMMC, laid out by the board's
# emmc.cfg (adsp-genimage.bbclass).

inherit adsp-genimage

IMAGE_TYPES += "emmc"
IMAGE_TYPEDEP:emmc = "ext4"

IMAGE_CMD:emmc = "adsp_genimage emmc"

do_image_emmc[depends] += " \
    genimage-native:do_populate_sysroot \
    e2fsprogs-native:do_populate_sysroot \
    ${ADSP_GENIMAGE_DEPENDS} \
"
do_image_emmc[file-checksums] += "${ADSP_GENIMAGE_DIR}/emmc.cfg:True"
