#!/bin/sh

cd /tmp

FILESIZE="1K 4K 8K 128K 512K 1M 4M 8M"
CIPHERS="aes-256-cbc des-cbc des3 aes-256-ecb"

#OpenSSL + cryptodev does not currently support des-ecb and des-ede3-ecb,
#So we cannot test des-ecb and des-ede3-ecb here

for i in ${FILESIZE}; do
	dd if=/dev/urandom of=secrets.txt.${i} bs=${i} count=1 > /dev/null 2>&1
done

#Don't salt the encryptions, so that we can compare operations with and without PKTE driver

for j in ${CIPHERS}; do

	rmmod cryptodev

	for i in ${FILESIZE}; do
		openssl enc -${j} -a -nosalt -in secrets.txt.${i} -out secrets.txt.enc.${i}.${j} -pass pass:test -provider legacy -provider default  > /dev/null 2>&1
		openssl enc -${j} -d -a -nosalt -in secrets.txt.enc.${i}.${j} -out secrets.txt.dec.${i}.${j} -pass pass:test -provider legacy -provider default  > /dev/null 2>&1
	done

	modprobe cryptodev

	for i in ${FILESIZE}; do
		openssl enc -${j} -a -nosalt -in secrets.txt.${i} -out secrets.txt.enc.${i}.${j}.pkte -pass pass:test -provider legacy -provider default -engine devcrypto  > /dev/null 2>&1
		openssl enc -${j} -d -a -nosalt -in secrets.txt.enc.${i}.${j}.pkte -out secrets.txt.dec.${i}.${j}.pkte -pass pass:test -provider legacy -provider default -engine devcrypto  > /dev/null 2>&1
	done

	for i in ${FILESIZE}; do
		cmp secrets.txt.enc.${i}.${j} secrets.txt.enc.${i}.${j}.pkte
		if [ $? -gt 0 ]; then
			echo "${j} [OpenSSL] (encrypt, ${i}): FAIL"
		else
			echo "${j} [OpenSSL] (encrypt, ${i}): PASS"
		fi
		rm secrets.txt.enc.${i}.${j} secrets.txt.enc.${i}.${j}.pkte

		cmp secrets.txt.dec.${i}.${j} secrets.txt.dec.${i}.${j}.pkte
		if [ $? -gt 0 ]; then
			echo "${j} [OpenSSL] (decrypt, ${i}): FAIL"
		else
			echo "${j} [OpenSSL] (decrypt, ${i}): PASS"
		fi
		rm secrets.txt.dec.${i}.${j} secrets.txt.dec.${i}.${j}.pkte
	done

done