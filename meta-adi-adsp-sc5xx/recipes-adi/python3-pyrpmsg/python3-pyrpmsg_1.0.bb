inherit python3-dir

LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""

SRC_URI = "\
	file://source/ \
	file://pyrpmsg-example.ipynb \
"

PV = "1.0"

S = "${WORKDIR}"
S_PYMODULES = "${S}/source"

DEPENDS = "python3 python3-build-native python3-installer-native"
RDEPENDS:${PN} = "python3-core python3-modules rpmsg-utils"

RPROVIDES:${PN} = "python3-pyrpmsg python3-remoteshell python3-adi_remote_rpmsg"

FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/adi_remote_rpmsg/* "
FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/remoteshell/* "
FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/pyrpmsg/* "
FILES:${PN} += " ${PYTHON_SITEPACKAGES_DIR}/utils.py "
FILES:${PN} += " /root/pyrpmsg-examples/ "

do_configure () {
	:
}

do_compile () {
	:
}

do_install () {
	# Install Jupyter Example
	install -d ${D}/root/pyrpmsg-examples/
	install ${S}/pyrpmsg-example.ipynb ${D}/root/pyrpmsg-examples/

	install -d ${D}${PYTHON_SITEPACKAGES_DIR}
	cp -r ${S_PYMODULES}/adi_remote_rpmsg ${D}${PYTHON_SITEPACKAGES_DIR}
	cp -r ${S_PYMODULES}/remoteshell ${D}${PYTHON_SITEPACKAGES_DIR}
	cp -r ${S_PYMODULES}/pyrpmsg ${D}${PYTHON_SITEPACKAGES_DIR}
	install ${S_PYMODULES}/utils.py ${D}${PYTHON_SITEPACKAGES_DIR}
}
