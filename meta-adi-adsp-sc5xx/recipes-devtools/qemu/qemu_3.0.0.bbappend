FILESEXTRAPATHS_prepend := "${THISDIR}/qemu:"
SRC_URI += " \
           file://0001-linux-user-assume-__NR_gettid-always-exists.patch \
           file://0001-linux-user-rename-gettid-to-sys_gettid-to-avoid-clas.patch \
           file://0002-linux-define-siocgstamp.patch \
           file://0003-linux-user-stime.patch \
	"
