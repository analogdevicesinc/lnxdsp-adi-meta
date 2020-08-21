
SOC_FAMILY_append = ":adsp-sc5xx"
SOC_FAMILY = "adi-soc"

require conf/machine/include/soc-family.inc
require conf/machine/include/tune-cortexa5.inc

# Increase this everytime you change something in the kernel
MACHINE_KERNEL_PR = "r0"

KERNEL_IMAGETYPE = "uImage"

PREFERRED_PROVIDER_virtual/kernel = "linux-adi"
PREFERRED_PROVIDER_virtual/bootloader = "${@ 'u-boot-adi-external' if "external" in d.getVar('TCMODE') else 'u-boot-adi'}"
PREFERRED_PROVIDER_u-boot = "${@ 'u-boot-adi-external' if "external" in d.getVar('TCMODE') else 'u-boot-adi'}"

PREFERRED_VERSION_diffutils = "3.3%"
PREFERRED_VERSION_oprofile = "1.1%"

EXTRA_IMAGEDEPENDS += "u-boot"

MACHINE_FEATURES = "kernel26 serial ethernet usbhost usbgadget mmc alsa"







