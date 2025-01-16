FILESEXTRAPATHS:prepend := "${THISDIR}/cryptodev-linux:"


SCARTHGAP_SRCREV = "bb8bc7cf60d2c0b097c8b3b0e807f805b577a53f"
UPSTREAM_SRCREV = "277d4574c10bb8e16ab6ab3f38b8e1cb6cd6c678"

UPSTREAM_COMPAT = "${@ True if ('adsp-sc598-som' in d.getVar('MACHINE')) else False}"
UPSTREAM = "${@bb.utils.contains('ADSP_KERNEL_TYPE','upstream', d.getVar('UPSTREAM_COMPAT'), False ,d)}"
SRCREV = "${@ d.getVar('UPSTREAM_SRCREV') if bb.utils.to_boolean(d.getVar('UPSTREAM')) else d.getVar('SCARTHGAP_SRCREV')}"

UPSTREAM_NON_COMPAT_PACTH = "file://0001-Disable-installing-header-file-provided-by-another-p.patch"
SRC_URI:remove = "${@d.getVar('UPSTREAM_NON_COMPAT_PACTH') if bb.utils.to_boolean(d.getVar('UPSTREAM')) else '' }"

SRC_URI += " \
    file://0001-Add-in-CRC32_HMAC-for-ADI-driver.patch \
"
