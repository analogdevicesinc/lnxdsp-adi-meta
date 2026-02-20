#!/bin/sh

echo "Flashing image..."

#Flash the eMMC
gunzip -c | dd of=/dev/mmcblk0 bs=512 > /dev/null 2<&1

echo "Image flashed..."

echo "Resizing partition to fill empty space..."

#File system check for safety
e2fsck -fp /dev/mmcblk0p1 > /dev/null 2<&1

#Resize the partition table
(
echo p 1   # Partition 1
echo d     # Delete
echo n     # New Partition (Recreate Partition 1)
echo p     # Partition
echo 1     # 1
echo 65536 # Starting Sector
echo       # Ending Sector (fill empty space)
echo w     # Write the table
echo q     # Quit
) |  fdisk /dev/mmcblk0 > /dev/null 2<&1

#File system check for safety
e2fsck -fp /dev/mmcblk0p1 > /dev/null 2<&1

#Resize the file system
resize2fs /dev/mmcblk0p1 > /dev/null 2<&1

echo "Resized!"

#Done
sync > /dev/null 2<&1

echo "Complete!"


