FILESEXTRAPATHS:prepend := "${THISDIR}/cryptodev-linux:"


SCARTHGAP_SRCREV = "bb8bc7cf60d2c0b097c8b3b0e807f805b577a53f"
UPSTREAM_SRCREV = "157a624d7a892ea5fb2df4bbd4e71c008adbecb7"

UPSTREAM_COMPAT = "${@ True if ('adsp-sc598-som' in d.getVar('MACHINE')) else False}"
UPSTREAM = "${@bb.utils.contains('ADSP_KERNEL_TYPE','upstream', d.getVar('UPSTREAM_COMPAT'), False ,d)}"
SRCREV = "${@ d.getVar('UPSTREAM_SRCREV') if bb.utils.to_boolean(d.getVar('UPSTREAM')) else d.getVar('SCARTHGAP_SRCREV')}"

SRC_URI += " \
    file://0001-Add-in-CRC32_HMAC-for-ADI-driver.patch \
"
