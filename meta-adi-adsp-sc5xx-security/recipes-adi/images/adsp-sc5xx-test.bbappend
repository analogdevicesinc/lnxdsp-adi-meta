inherit adsp-signed-fit

IMAGE_INSTALL:append = " ${@bb.utils.contains('DISTRO_FEATURES','optee','libp11 opensc openssl-bin packagegroup-adsp-sc5xx-security-tests','',d)}"

# OP-TEE makes the packet engine secure-only, so Linux cannot run the PKTE tests
IMAGE_INSTALL:remove = "${@bb.utils.contains('DISTRO_FEATURES','optee','crypto-tests-ptest','',d)}"
