# use pseudo 1.9.11+git.
#
# release < 5.0.0 pseudo fails under fakeroot with:
#   got *at() syscall for unknown directory
#   unknown base path for fd
#   tar: ... Cannot mkdir: Bad address
#
# pseudo-1.9.11 to handle updated fd/openat/close_range
# and remove old recipe patches that do not apply anymore.
SRCREV = "ba8887e5f1e922f866681ec7dec1a00b602a9328"
PV = "1.9.11+git"

SRC_URI:remove = " \
    file://0001-configure-Prune-PIE-flags.patch \
    file://glibc238.patch \
    file://older-glibc-symbols.patch \
"
