# Builds an image with genimage, for the adsp-image-* image types. Each board
# has its own set of genimage configs, files/genimage/<board>/<type>.cfg in any
# layer, one for each of its boot media. ADSP_GENIMAGE_BOARD names the set,
# and ADSP_GENIMAGE_TYPES, in the machine configuration, the image types it
# builds. The configs lay out:
#
#   u-boot-spl.ldr, u-boot.ldr, fitImage   from DEPLOY_DIR_IMAGE
#   rootfs.erofs, rootfs.ext4              the root filesystem images
#   rootfs.ubi                             rootfs.erofs in a static UBI volume
#   /boot                                  a directory holding fitImage, for
#                                          the filesystems genimage creates

ADSP_GENIMAGE_BOARD ?= "${MACHINE}"
ADSP_GENIMAGE_DIR = "${@bb.utils.which(d.getVar('BBPATH'), 'files/genimage/' + d.getVar('ADSP_GENIMAGE_BOARD'))}"

# Tasks that deploy u-boot-spl.ldr and u-boot.ldr. fitImage comes from the
# image's own do_assemble_fitimage, which runs before the image types.
ADSP_GENIMAGE_DEPENDS ?= "virtual/bootloader:do_deploy adsp-boot:do_deploy"

# adsp_genimage <type>: build <type>.img from the board's <type>.cfg and
# deploy it as ${IMAGE_NAME}.<type>
adsp_genimage() {
    cfg=${ADSP_GENIMAGE_DIR}/$1.cfg
    if [ ! -f $cfg ]; then
        bbfatal "No genimage config files/genimage/${ADSP_GENIMAGE_BOARD}/$1.cfg for the $1 image"
    fi

    work=${WORKDIR}/genimage-$1
    rm -rf $work
    mkdir -p $work/input $work/output $work/root/boot $work/tmp

    ln -s ${DEPLOY_DIR_IMAGE}/u-boot-spl.ldr ${DEPLOY_DIR_IMAGE}/u-boot.ldr \
        ${DEPLOY_DIR_IMAGE}/fitImage $work/input/
    ln -s ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.erofs-lz4hc $work/input/rootfs.erofs
    ln -s ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.ext4 $work/input/rootfs.ext4
    ln -s ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.erofs-lz4hc.ubi $work/input/rootfs.ubi
    cp ${DEPLOY_DIR_IMAGE}/fitImage $work/root/boot/

    genimage --config $cfg \
        --rootpath $work/root --tmppath $work/tmp \
        --inputpath $work/input --outputpath $work/output

    mv $work/output/$1.img ${IMGDEPLOYDIR}/${IMAGE_NAME}.$1
}

# Only the configs matter, not where the layer is checked out
adsp_genimage[vardepsexclude] += "ADSP_GENIMAGE_DIR"
adsp_genimage[vardeps] += "ADSP_GENIMAGE_BOARD"
