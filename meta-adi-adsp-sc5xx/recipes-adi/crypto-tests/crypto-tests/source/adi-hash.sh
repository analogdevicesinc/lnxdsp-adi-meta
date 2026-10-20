#!/bin/sh

HW_DRIVER="@ADI_HW_DRIVER@"
DIGESTS="sha256 sha224 sha1 md5"
SIZES="127bytes 128kbytes 32mbytes"
rc=0

result() {
	if [ "$2" -eq 0 ]; then
		echo "PASS: $1"
	else
		echo "FAIL: $1"
		rc=1
	fi
}

size_label() {
	case "$1" in
	127bytes) echo "127 bytes" ;;
	128kbytes) echo "128 kilobytes" ;;
	32mbytes) echo "32 megabytes" ;;
	esac
}

WORKDIR=$(mktemp -d) || exit 1
trap 'rm -rf "${WORKDIR}"' EXIT
cd "${WORKDIR}" || exit 1

if [ -n "${HW_DRIVER}" ]; then
	for alg in ${DIGESTS}; do
		awk -v n="${alg}" '$1 == "name" { nm = $3 } $1 == "driver" && nm == n { print $3 }' /proc/crypto | grep -q -F "${HW_DRIVER}"
		result "${alg} [${HW_DRIVER}] registered" $?
	done
fi

# Reference digests, computed without cryptodev loaded
rmmod cryptodev 2>/dev/null

dd if=/dev/urandom of=127bytes bs=127 count=1 > /dev/null 2>&1
dd if=/dev/urandom of=128kbytes bs=128K count=1 > /dev/null 2>&1
dd if=/dev/urandom of=32mbytes bs=1M count=32 > /dev/null 2>&1

for size in ${SIZES}; do
	for alg in ${DIGESTS}; do
		openssl dgst -${alg} < ${size} > sw_${size}_${alg}
	done
done

modprobe cryptodev

# Software only: the devcrypto engine skips hash drivers without export/import
for alg in ${DIGESTS}; do
	for size in ${SIZES}; do
		openssl dgst -${alg} -engine devcrypto < ${size} > engine_${size}_${alg} 2>/dev/null
		[ -s sw_${size}_${alg} ] && cmp -s engine_${size}_${alg} sw_${size}_${alg}
		status=$?
		result "$(echo ${alg} | tr a-z A-Z) [OpenSSL, software] ($(size_label ${size}))" ${status}
	done
done

echo "Running tests using /dev/crypto + ioctls (adi-hash.c program):"
echo ""
/crypto/adi-hash || rc=1

exit ${rc}
