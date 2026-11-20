# "usb" image type: an image of a whole USB mass storage device, laid out by
# the board's usb.cfg (adsp-genimage.bbclass).

inherit adsp-genimage

IMAGE_TYPES += "usb"
IMAGE_TYPEDEP:usb = "ext4"

IMAGE_CMD:usb = "adsp_genimage usb"

do_image_usb[depends] += " \
    genimage-native:do_populate_sysroot \
    e2fsprogs-native:do_populate_sysroot \
    ${ADSP_GENIMAGE_DEPENDS} \
"
do_image_usb[file-checksums] += "${ADSP_GENIMAGE_DIR}/usb.cfg:True"
