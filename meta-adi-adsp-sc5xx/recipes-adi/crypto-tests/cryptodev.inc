FILESEXTRAPATHS:prepend := "${THISDIR}/cryptodev-linux:"

SRC_URI += " \
	file://0001-Add-in-CRC32_HMAC-for-ADI-driver.patch \
	file://fix-build-for-Linux-5.11-rc1.patch \
"
