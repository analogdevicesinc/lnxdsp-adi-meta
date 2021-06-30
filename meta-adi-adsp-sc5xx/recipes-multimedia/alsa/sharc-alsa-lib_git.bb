LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""

SRC_URI = "git://git@src.timesys.com/services/analog-devices/sharc-alsa-audio-project/sharc-alsa-lib.git;protocol=ssh"

PV = "1.0+git${SRCPV}"
SRCREV = "e2800902f177f10133be72559aac90fafd0f8d02"
S = "${WORKDIR}/git"

inherit cmake

PACKAGES_prepend= "${PN}-example "
FILES_${PN}-example = "/usr/bin/sharc-alsa-example"
