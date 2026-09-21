FILESEXTRAPATHS:prepend := "${THISDIR}/cryptodev-linux:"

NON_COMPAT_PACTH = "file://0001-Disable-installing-header-file-provided-by-another-p.patch"
SRC_URI:remove = "${@d.getVar('NON_COMPAT_PACTH')}"

SRC_URI += " \
    file://0001-Add-in-CRC32_HMAC-for-ADI-driver.patch \
"
