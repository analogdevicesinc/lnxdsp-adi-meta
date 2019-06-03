require linux-adi.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

PR = "r0"

SRC_URI += " \
	file://0001-Fix-errors-for-compiling-with-GCC8.patch \
	file://0002-Add-NFS-client-file-system-support-in-to-4.16-kernel.patch \
	file://0003-Add-in-SC58x-SC57x-CPU-freq-driver.patch \
	file://0004-Add-in-Blackfin-Hardware-CRC-crypto-driver.patch \
	file://0005-Add-in-ADAU1962-ADAU1979-as-built-ins-instead-of-mod.patch \
	file://0006-Pull-ADAU1962-enable-low-with-a-gpio-hog-so-that-it-.patch \
	file://0007-Add-in-machine-specific-SRAM-files-for-SC57x-SC58x-b.patch \
"

#If running DMA tests, then include the following patch as well (this disables CONFIG_ICC and enables CONFIG_ARCH_SRAM_ALLOC):
#SRC_URI += " \
#	file://Remove-CONFIG_ICC-and-use-CONFIG_ARCH_SRAM_ALLOC-for.patch \
#"
