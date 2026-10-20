#!/bin/sh

HW_DRIVER="@ADI_HW_DRIVER@"
rc=0

result() {
	if [ "$2" -eq 0 ]; then
		echo "PASS: $1"
	else
		echo "FAIL: $1"
		rc=1
	fi
}

WORKDIR=$(mktemp -d) || exit 1
trap 'rm -rf "${WORKDIR}"' EXIT
cd "${WORKDIR}" || exit 1

# Kernel algorithms behind the CIPHERS below
if [ -n "${HW_DRIVER}" ]; then
	for alg in "cbc(aes)" "cbc(des)" "cbc(des3_ede)" "ecb(aes)"; do
		awk -v n="${alg}" '$1 == "name" { nm = $3 } $1 == "driver" && nm == n { print $3 }' /proc/crypto | grep -q -F "${HW_DRIVER}"
		result "${alg} [${HW_DRIVER}] registered" $?
	done
fi

modprobe cryptodev

# Without the engine openssl silently falls back to software
openssl engine -t devcrypto 2>&1 | grep -q "\[ available \]"
result "devcrypto engine [OpenSSL]" $?

FILESIZE="1K 4K 8K 128K 512K 1M 4M 8M"
CIPHERS="aes-256-cbc des-cbc des3 aes-256-ecb"

#OpenSSL + cryptodev does not currently support des-ecb and des-ede3-ecb,
#So we cannot test des-ecb and des-ede3-ecb here

for i in ${FILESIZE}; do
	dd if=/dev/urandom of=secrets.txt.${i} bs=${i} count=1 > /dev/null 2>&1
done

#Don't salt the encryptions, so that we can compare operations with and without PKTE driver

for j in ${CIPHERS}; do

	rmmod cryptodev 2>/dev/null

	for i in ${FILESIZE}; do
		openssl enc -${j} -a -nosalt -in secrets.txt.${i} -out secrets.txt.enc.${i}.${j} -pass pass:test -provider legacy -provider default  > /dev/null 2>&1
	done

	modprobe cryptodev

	for i in ${FILESIZE}; do
		openssl enc -${j} -a -nosalt -in secrets.txt.${i} -out secrets.txt.enc.${i}.${j}.pkte -pass pass:test -provider legacy -provider default -engine devcrypto  > /dev/null 2>&1
		openssl enc -${j} -d -a -nosalt -in secrets.txt.enc.${i}.${j} -out secrets.txt.dec.${i}.${j}.pkte -pass pass:test -provider legacy -provider default -engine devcrypto  > /dev/null 2>&1
	done

	for i in ${FILESIZE}; do
		[ -s secrets.txt.enc.${i}.${j} ] && cmp -s secrets.txt.enc.${i}.${j} secrets.txt.enc.${i}.${j}.pkte
		result "${j} [OpenSSL] (encrypt, ${i})" $?

		cmp -s secrets.txt.${i} secrets.txt.dec.${i}.${j}.pkte
		result "${j} [OpenSSL] (decrypt, ${i})" $?

		rm -f secrets.txt.enc.${i}.${j} secrets.txt.enc.${i}.${j}.pkte secrets.txt.dec.${i}.${j}.pkte
	done

done

exit ${rc}
