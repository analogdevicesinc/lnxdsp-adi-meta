inherit python3-dir

LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""

SRC_URI = "git://github.com/OliverGaskellADI/pyrpmsg.git;protocol=https;branch=main"

# Modify these as desired
PV = "1.0+git"
SRCREV = "b9ea20c1c1f38be238eaaf1337df0c0a5e13b497"

S = "${WORKDIR}/git"

DEPENDS = "python3 python3-build-native python3-installer-native"
RDEPENDS_${PN} = "python3-core python3-modules rpmsg-utils"

RPROVIDES_${PN} = "python3-pyrpmsg python3-remoteshell python3-adi_remote_rpmsg"

FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/adi_remote_rpmsg/* "
FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/remoteshell/* "
FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/pyrpmsg/* "
FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/utils.py "

# NOTE: no Makefile found, unable to determine what needs to be done

do_configure () {
	# Specify any needed configure commands here
	:
}

do_compile () {
	# Specify compilation commands here
	:
}

do_install () {
	# Specify install commands here
	echo ${FILES_${PN}}
	install -d ${D}${PYTHON_SITEPACKAGES_DIR}
	cp -r ${S}/adi_remote_rpmsg ${D}${PYTHON_SITEPACKAGES_DIR}
	cp -r ${S}/remoteshell ${D}${PYTHON_SITEPACKAGES_DIR}
	cp -r ${S}/pyrpmsg ${D}${PYTHON_SITEPACKAGES_DIR}
	install ${S}/utils.py ${D}${PYTHON_SITEPACKAGES_DIR}
}
