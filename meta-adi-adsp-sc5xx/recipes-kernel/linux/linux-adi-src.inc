KERNEL_GIT_URI = "git://bitbucket.analog.com/scm/dte/linux.git"
KERNEL_GIT_PROTOCOL = "https"

# Put a local version until we have a true SRCREV to point to
SRCBRANCH ?= ""

SRC_URI += " \
	${KERNEL_GIT_URI};protocol=${KERNEL_GIT_PROTOCOL};branch=${SRCBRANCH} \
"
