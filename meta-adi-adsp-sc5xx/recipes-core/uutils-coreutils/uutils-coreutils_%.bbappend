# The default PACKAGECONFIG pulls in systemd from DISTRO_FEATURES, which makes the
# native variant DEPEND on the non-existent systemd-native and leaves
# uutils-coreutils-native unbuildable. The systemd logind integration is only
# meaningful for target, so drop it for native/nativesdk builds.
PACKAGECONFIG:remove:class-native = "systemd"
PACKAGECONFIG:remove:class-nativesdk = "systemd"
