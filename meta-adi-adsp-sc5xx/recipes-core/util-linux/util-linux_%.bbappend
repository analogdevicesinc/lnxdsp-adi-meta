# systemd.bbclass unconditionally appends systemd-systemctl-native to DEPENDS for
# every recipe inheriting systemd when systemd is in DISTRO_FEATURES, including
# the native variant. systemd-systemctl-native itself DEPENDS on util-linux-native,
# so util-linux-native <-> systemd-systemctl-native form a circular dependency
# (surfaces in do_create_recipe_spdx). Native variants never install systemd unit
# scripts, so drop the spurious dependency for them.
DEPENDS:remove:class-native = "systemd-systemctl-native"
DEPENDS:remove:class-nativesdk = "systemd-systemctl-native"
PACKAGE_WRITE_DEPS:remove:class-native = "systemd-systemctl-native"
PACKAGE_WRITE_DEPS:remove:class-nativesdk = "systemd-systemctl-native"
