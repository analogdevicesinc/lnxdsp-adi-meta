#!/bin/sh

cd /tmp

LIST="1K 4K 8K 128K 512K 1M 4M 8M"

for i in ${LIST}; do
	dd if=/dev/urandom of=secrets.txt.${i} bs=${i} count=1 > /dev/null 2>&1
done

#PBKDF2="-pbkdf2"
PBKDF2=""

#Don't salt the encryptions, so that we can compare operations with and without PKTE driver

rmmod cryptodev

for i in ${LIST}; do
	openssl aes-256-cbc -a -nosalt ${PBKDF2} -in secrets.txt.${i} -out secrets.txt.enc.${i} -pass pass:test
	openssl aes-256-cbc -d -a -nosalt ${PBKDF2} -in secrets.txt.enc.${i} -out secrets.txt.dec.${i} -pass pass:test
done

modprobe cryptodev

for i in ${LIST}; do
	openssl aes-256-cbc -a -nosalt ${PBKDF2} -in secrets.txt.${i} -out secrets.txt.enc.${i}.pkte -pass pass:test
	openssl aes-256-cbc -d -a -nosalt ${PBKDF2} -in secrets.txt.enc.${i}.pkte -out secrets.txt.dec.${i}.pkte -pass pass:test
done


for i in ${LIST}; do
	cmp secrets.txt.enc.${i} secrets.txt.enc.${i}.pkte
	if [ $? -gt 0 ]; then
		echo "AES-CBC [OpenSSL] (encrypt, ${i}): FAIL"
	else
		echo "AES-CBC [OpenSSL] (encrypt, ${i}): PASS"
	fi

	cmp secrets.txt.dec.${i} secrets.txt.dec.${i}.pkte
	if [ $? -gt 0 ]; then
		echo "AES-CBC [OpenSSL] (decrypt, ${i}): FAIL"
	else
		echo "AES-CBC [OpenSSL] (decrypt, ${i}): PASS"
	fi
done


