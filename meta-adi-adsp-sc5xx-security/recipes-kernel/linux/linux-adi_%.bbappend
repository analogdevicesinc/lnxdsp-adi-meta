FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Security (OP-TEE) builds only: apply the secure-world device-tree
# configuration (disable SEC/RCU-reboot/SHARC-remoteproc/TRNG/crypto that the
# secure world owns, add PSCI + OP-TEE nodes) and the matching kernel config.
# Without this the non-secure kernel faults (SError) in adi_sec_probe.
SRC_URI:append:adsp-sc5xx-optee = " \
    file://0001-sc598-Security-configuration-device-tree.patch \
    file://sc598-security.cfg \
"
