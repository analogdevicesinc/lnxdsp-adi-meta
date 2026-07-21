
# Based on distro features selected in the distro definition, enable overrides that will control
# the behavior of other recipes

DISTROOVERRIDES = "${@bb.utils.contains('DISTRO_FEATURES', 'optee', ':adsp-sc5xx-optee', '', d)}${@bb.utils.contains('DISTRO_FEATURES', 'signedboot', ':adsp-sc5xx-signedboot', '', d)}"
DISTROOVERRIDES:append = "${@bb.utils.contains('DISTRO_FEATURES', 'optee-shim', ':optee-shim', '', d)}"

BASH_HAS_SECUREBOOT = "${@bb.utils.contains('DISTRO_FEATURES', 'signedboot', '1', '', d)}"
