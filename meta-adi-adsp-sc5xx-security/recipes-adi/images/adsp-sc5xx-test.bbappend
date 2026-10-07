inherit adsp-signed-fit

IMAGE_INSTALL:append = " ${@bb.utils.contains('DISTRO_FEATURES','optee','libp11 opensc openssl-bin packagegroup-adsp-sc5xx-security-tests','',d)}"
