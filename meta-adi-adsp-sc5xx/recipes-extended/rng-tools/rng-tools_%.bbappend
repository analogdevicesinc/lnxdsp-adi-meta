FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

#Disable RNDR, not available on our ARM64
SRC_URI += "file://0001-Disable-RNDR-as-this-is-not-available-on-our-ARMv8.2.patch"

#Disable jitter entropy generation/initialization (software based and takes too long)
EXTRA_OECONF:append=" --disable-jitterentropy"