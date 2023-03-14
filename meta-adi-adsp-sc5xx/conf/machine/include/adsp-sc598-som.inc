SOC_FAMILY:append = ":adsp-sc5xx"
SOC_FAMILY = "adi-soc"

require conf/machine/include/soc-family.inc
require tune-cortexa55.inc

# Increase this everytime you change something in the kernel
MACHINE_KERNEL_PR = "r0"

KERNEL_IMAGETYPE = "Image.gz"

PREFERRED_PROVIDER_virtual/kernel = "linux-adi"
PREFERRED_PROVIDER_virtual/bootloader = "u-boot-adi"

EXTRA_IMAGEDEPENDS += "virtual/bootloader"

MACHINE_FEATURES = "kernel26 serial ethernet usbhost usbgadget mmc alsa watchdog"
