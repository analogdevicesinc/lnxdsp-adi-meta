#!/bin/sh

HW_DRIVER="@ADI_HW_DRIVER@"
rc=0

if [ -n "${HW_DRIVER}" ]; then
	if awk '$1 == "name" { nm = $3 } $1 == "driver" && nm == "hmac(crc32)" { print $3 }' /proc/crypto | grep -q -F "${HW_DRIVER}"; then
		echo "PASS: hmac(crc32) [${HW_DRIVER}] registered"
	else
		echo "FAIL: hmac(crc32) [${HW_DRIVER}] registered"
		rc=1
	fi
fi

modprobe cryptodev

echo "Running tests using /dev/crypto + ioctls (adi-crc.c program):"
echo ""
/crypto/adi-crc || rc=1

exit ${rc}
