inherit python3-dir

LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""

SRC_URI = "file://source/"

PV = "1.0"

S = "${WORKDIR}/source"

DEPENDS = "python3 python3-build-native python3-installer-native"
RDEPENDS_${PN} = "python3-core python3-modules rpmsg-utils"

RPROVIDES_${PN} = "python3-pyrpmsg python3-remoteshell python3-adi_remote_rpmsg"

FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/adi_remote_rpmsg/* "
FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/remoteshell/* "
FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/pyrpmsg/* "
FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/utils.py "

do_configure () { 
	:
}

do_compile () {
	:
}

do_install () {
	install -d ${D}${PYTHON_SITEPACKAGES_DIR}
	cp -r ${S}/adi_remote_rpmsg ${D}${PYTHON_SITEPACKAGES_DIR}
	cp -r ${S}/remoteshell ${D}${PYTHON_SITEPACKAGES_DIR}
	cp -r ${S}/pyrpmsg ${D}${PYTHON_SITEPACKAGES_DIR}
	install ${S}/utils.py ${D}${PYTHON_SITEPACKAGES_DIR}
}
