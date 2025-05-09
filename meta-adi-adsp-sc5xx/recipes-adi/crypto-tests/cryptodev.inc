FILESEXTRAPATHS:prepend := "${THISDIR}/cryptodev-linux:"


SRCREV = "277d4574c10bb8e16ab6ab3f38b8e1cb6cd6c678"

NON_COMPAT_PACTH = "file://0001-Disable-installing-header-file-provided-by-another-p.patch"
SRC_URI:remove = "${@d.getVar('NON_COMPAT_PACTH')}"

SRC_URI += " \
    file://0001-Add-in-CRC32_HMAC-for-ADI-driver.patch \
"
