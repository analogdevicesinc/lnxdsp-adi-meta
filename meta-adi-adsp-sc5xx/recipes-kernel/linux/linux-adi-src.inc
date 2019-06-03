KERNEL_GIT_URI = "git://git@src.timesys.com/services/analog-devices/analog-devices-yocto-bsp-porting/linux-4.16.git"
KERNEL_GIT_PROTOCOL = "ssh"

BRANCH = "develop/linuxaddin-1.4.0"
SRCREV = "485f6546b3b45a1b8b88db02cd0012810413948b"

SRC_URI += " \
	${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${BRANCH} \
"