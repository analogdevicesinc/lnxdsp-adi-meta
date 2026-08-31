FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:adsp-sc598-som-ezkit = "${@bb.utils.contains('DISTRO_FEATURES', 'signedboot', ' file://0001-sc598-Security-configuration.patch file://0002-sc598-Enlarge-u-boot-flash-partition-for-secure-boot.patch', '', d)}"
