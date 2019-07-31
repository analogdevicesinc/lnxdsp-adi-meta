KERNEL_GIT_URI = "git://git@src.timesys.com/services/analog-devices/analog-devices-yocto-bsp-porting/linux-4.16.git"
KERNEL_GIT_PROTOCOL = "ssh"

BRANCH = "develop/linuxaddin-1.4.0"
SRCREV = "47fe1e4aa04d7c76f825057193f6136268359066"

SRC_URI += " \
	${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${BRANCH} \
"
