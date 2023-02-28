PACKAGECONFIG[cryptodev-linux] = "enable-devcryptoeng,disable-devcryptoeng,cryptodev-linux"
PACKAGECONFIG_append = " cryptodev-linux"

#Pull in proper cryptodev.h header
DEPENDS += "cryptodev-linux"