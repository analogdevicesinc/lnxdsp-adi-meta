FILESEXTRAPATHS_prepend := "${THISDIR}/busybox:"

SRC_URI += " \
	file://inittab \
"
do_install_append() {
	cd ${D}${sysconfdir}
	sed -i '/::respawn:/d' inittab 
	sed -i '$a ttySC0::respawn:/bin/sh' inittab
}