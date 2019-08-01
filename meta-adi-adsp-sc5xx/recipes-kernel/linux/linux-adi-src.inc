KERNEL_GIT_URI = "git://bitbucket.analog.com/scm/dte/linux.git"
KERNEL_GIT_PROTOCOL = "https"

BRANCH = "develop/linuxaddin-1.4.0"
SRCREV = "${AUTOREV}"

SRC_URI += " \
	${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${BRANCH} \
"
