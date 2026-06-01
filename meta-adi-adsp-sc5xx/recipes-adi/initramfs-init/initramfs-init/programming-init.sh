#!/bin/sh

# Bootstrap programming init for ADSP-SC5xx
# Runs as PID 1 from initramfs when booted via bootm.

PATH=/sbin:/bin:/usr/sbin:/usr/bin

mkdir -p /proc
mkdir -p /sys
mkdir -p /dev

mount -t proc proc /proc
mount -t sysfs sys /sys
mount -t devtmpfs devtmpfs /dev

set -e

FW_DIR="${FIRMWARE_DIR:-/usr/firmware}"

echo

echo "======== Bootstrap Programming ========"
echo "Using firmware payload from ${FW_DIR}"

for fw in u-boot-spl.ldr u-boot.ldr fitImage rootfs.ubi; do
    if [ ! -f "${FW_DIR}/${fw}" ]; then
        echo "ERROR: Missing required payload ${FW_DIR}/${fw}"
        exec sh
    fi
done

echo "Programming SPI flash..."

echo "Installing U-Boot SPL to mtd0"
flash_erase /dev/mtd0 0 0
flashcp "${FW_DIR}/u-boot-spl.ldr" /dev/mtd0

echo "Installing U-Boot to mtd1"
flash_erase /dev/mtd1 0 0
flashcp "${FW_DIR}/u-boot.ldr" /dev/mtd1

echo "Installing Linux fitImage to mtd2"
flash_erase /dev/mtd2 0 0
flashcp "${FW_DIR}/fitImage" /dev/mtd2

echo "Installing rootfs UBI to mtd3"
ubiformat /dev/mtd3 -f "${FW_DIR}/rootfs.ubi" -y

sync

echo "Programming complete. Rebooting in 3 seconds..."
sleep 3
reboot -f
