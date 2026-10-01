# genimage-native (see recipes-support/genimage) needs libconfuse-native.
BBCLASSEXTEND = "native nativesdk"

# libconfuse runs autogen.sh (autoreconf -> autopoint) in do_configure and then
# regenerates its flex lexer, so the native build needs gettext/flex/bison natives.
DEPENDS:append:class-native = " gettext-native flex-native bison-native"
DEPENDS:append:class-nativesdk = " gettext-native flex-native bison-native"
