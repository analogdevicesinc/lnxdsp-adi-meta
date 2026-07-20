
# If building optee support, enable it and include it in stage 2 image
DDEPENDS:append:adsp-sc598-som-ezkit = " ${@bb.utils.contains('DISTRO_FEATURES','optee','trusted-firmware-a optee-os-elf','',d)}"
# todo: when building without uboot support include kernel "elf" version
#DDEPENDS:append:adsp-sc598-som-ezkit = " linux-adi-elf"

STAGE_2_SRC:adsp-sc598-som-ezkit = "u-boot-proper-${BOARD}.elf ${@bb.utils.contains('DISTRO_FEATURES','optee','tee.elf bl31.elf','',d)}"
# todo: distro override option to build without the uboot support in sc598
#STAGE_2_SRC:adsp-sc598-som-ezkit = "kernel.elf dtb.elf initrd.elf tee.elf bl31.elf"

# This needs to be last to jump to it
STAGE_2_SRC:append:optee-shim = " optee-shim.elf"

# If we'll be signing the output later, call it unsigned for the signing recipe
# to be able to find it
STAGE_2_TARGET_NAME:adsp-sc5xx-signedboot = "u-boot-unsigned.ldr"
