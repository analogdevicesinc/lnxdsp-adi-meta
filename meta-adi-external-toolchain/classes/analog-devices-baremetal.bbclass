setup_analog_devices_baremetal(){
    unset BUILD_LDFLAGS
    unset BUILD_CFLAGS
    unset CXXFLAGS
    unset OBJCOPY
    unset BUILD_CPPFLAGS
    unset TARGET_CXXFLAGS
    unset LDFLAGS
    unset FC
    unset STRINGS
    unset CPP
    unset STRIP
    unset AR
    unset PKG_CONFIG_DIR
    unset READELF
    unset AS
    unset BUILD_CXXFLAGS
    unset CFLAGS
    unset TARGET_LDFLAGS
    unset TARGET_CFLAGS
    unset CPPFLAGS
    unset CXX
    unset LD
    unset RANLIB
    unset CC
    unset PKG_CONFIG_LIBDIR
    unset NM
    unset OBJDUMP
    unset CCLD
    
    export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:${EXTERNAL_TOOLCHAIN_BAREMETAL}

}
