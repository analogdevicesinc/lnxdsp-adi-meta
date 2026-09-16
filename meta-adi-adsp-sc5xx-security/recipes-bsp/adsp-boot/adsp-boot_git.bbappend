
# If building optee support, enable it and include it in stage 2 image
DDEPENDS:append:adsp-sc598-som-ezkit = " ${@bb.utils.contains('DISTRO_FEATURES','optee','trusted-firmware-a optee-os-elf','',d)}"

# Stage-2 payloads, jump target LAST. With ADI_OS_BOOT the stage-2 is just
# OP-TEE + BL31 (SPL -> BL31(TF-A) -> BL32(OP-TEE) -> kernel); BL31 reads the
# kernel FIT from SPI so U-Boot proper is never entered. Otherwise U-Boot proper
# is BL33.
ADI_STAGE2_OS_BOOT = "${@bb.utils.contains('DISTRO_FEATURES','optee','tee.elf bl31.elf','',d)}"
ADI_STAGE2_UBOOT = "u-boot-proper-${BOARD}.elf ${@bb.utils.contains('DISTRO_FEATURES','optee','tee.elf bl31.elf','',d)}"
STAGE_2_SRC:adsp-sc598-som-ezkit = "${@d.getVar('ADI_STAGE2_OS_BOOT') if bb.utils.to_boolean(d.getVar('ADI_OS_BOOT')) else d.getVar('ADI_STAGE2_UBOOT')}"

# This needs to be last to jump to it
STAGE_2_SRC:append:optee-shim = " optee-shim.elf"

# If we'll be signing the output later, call it unsigned for the signing recipe
# to be able to find it
STAGE_2_TARGET_NAME:adsp-sc5xx-signedboot = "u-boot-unsigned.ldr"
