#!/bin/sh

rmmod cryptodev

dd if=/dev/urandom of=/tmp/127bytes bs=127 count=1 > /dev/null 2>&1
dd if=/dev/urandom of=/tmp/128kbytes bs=128K count=1 > /dev/null 2>&1
dd if=/dev/urandom of=/tmp/32mbytes bs=128K count=1 > /dev/null 2>&1

openssl dgst -sha256  < /tmp/127bytes > /tmp/sw_127bytes_sha256
openssl dgst -sha224  < /tmp/127bytes > /tmp/sw_127bytes_sha224
openssl dgst -sha1  < /tmp/127bytes > /tmp/sw_127bytes_sha1
openssl dgst -md5 < /tmp/127bytes > /tmp/sw_127bytes_md5

openssl dgst -sha256  < /tmp/128kbytes > /tmp/sw_128kbytes_sha256
openssl dgst -sha224  < /tmp/128kbytes > /tmp/sw_128kbytes_sha224
openssl dgst -sha1  < /tmp/128kbytes > /tmp/sw_128kbytes_sha1
openssl dgst -md5  < /tmp/128kbytes > /tmp/sw_128kbytes_md5

openssl dgst -sha256  < /tmp/32mbytes > /tmp/sw_32mbytes_sha256
openssl dgst -sha224  < /tmp/32mbytes > /tmp/sw_32mbytes_sha224
openssl dgst -sha1  < /tmp/32mbytes > /tmp/sw_32mbytes_sha1
openssl dgst -md5  < /tmp/32mbytes > /tmp/sw_32mbytes_md5

modprobe cryptodev

openssl dgst -sha256 -engine devcrypto < /tmp/127bytes > /tmp/hw_127bytes_sha256
openssl dgst -sha224 -engine devcrypto < /tmp/127bytes > /tmp/hw_127bytes_sha224
openssl dgst -sha1 -engine devcrypto < /tmp/127bytes > /tmp/hw_127bytes_sha1
openssl dgst -md5 -engine devcrypto< /tmp/127bytes > /tmp/hw_127bytes_md5

openssl dgst -sha256 -engine devcrypto < /tmp/128kbytes > /tmp/hw_128kbytes_sha256
openssl dgst -sha224 -engine devcrypto < /tmp/128kbytes > /tmp/hw_128kbytes_sha224
openssl dgst -sha1 -engine devcrypto < /tmp/128kbytes > /tmp/hw_128kbytes_sha1
openssl dgst -md5 -engine devcrypto < /tmp/128kbytes > /tmp/hw_128kbytes_md5

openssl dgst -sha256 -engine devcrypto < /tmp/32mbytes > /tmp/hw_32mbytes_sha256
openssl dgst -sha224 -engine devcrypto < /tmp/32mbytes > /tmp/hw_32mbytes_sha224
openssl dgst -sha1 -engine devcrypto < /tmp/32mbytes > /tmp/hw_32mbytes_sha1
openssl dgst -md5 -engine devcrypto < /tmp/32mbytes > /tmp/hw_32mbytes_md5

cmp /tmp/hw_127bytes_sha256 /tmp/sw_127bytes_sha256
if [ $? -gt 0 ]; then
	echo "SHA256 [OpenSSL] (127 bytes): FAIL"
else
	echo "SHA256 [OpenSSL] (127 bytes): PASS"
fi

cmp /tmp/hw_128kbytes_sha256 /tmp/sw_128kbytes_sha256
if [ $? -gt 0 ]; then
	echo "SHA256 [OpenSSL] (128 kilobytes): FAIL"
else
	echo "SHA256 [OpenSSL] (128 kilobytes): PASS"
fi

cmp /tmp/hw_32mbytes_sha256 /tmp/sw_32mbytes_sha256
if [ $? -gt 0 ]; then
	echo "SHA256 [OpenSSL] (32 megabytes): FAIL"
else
	echo "SHA256 [OpenSSL] (32 megabytes): PASS"
fi

cmp /tmp/hw_127bytes_sha224 /tmp/sw_127bytes_sha224
if [ $? -gt 0 ]; then
	echo "SHA224 [OpenSSL] (127 bytes): FAIL"
else
	echo "SHA224 [OpenSSL] (127 bytes): PASS"
fi

cmp /tmp/hw_128kbytes_sha224 /tmp/sw_128kbytes_sha224
if [ $? -gt 0 ]; then
	echo "SHA224 [OpenSSL] (128 kilobytes): FAIL"
else
	echo "SHA224 [OpenSSL] (128 kilobytes): PASS"
fi

cmp /tmp/hw_32mbytes_sha224 /tmp/sw_32mbytes_sha224
if [ $? -gt 0 ]; then
	echo "SHA224 [OpenSSL] (32 megabytes): FAIL"
else
	echo "SHA224 [OpenSSL] (32 megabytes): PASS"
fi

cmp /tmp/hw_127bytes_sha1 /tmp/sw_127bytes_sha1
if [ $? -gt 0 ]; then
	echo "SHA1 [OpenSSL] (127 bytes): FAIL"
else
	echo "SHA1 [OpenSSL] (127 bytes): PASS"
fi

cmp /tmp/hw_128kbytes_sha1 /tmp/sw_128kbytes_sha1
if [ $? -gt 0 ]; then
	echo "SHA1 [OpenSSL] (128 kilobytes): FAIL"
else
	echo "SHA1 [OpenSSL] (128 kilobytes): PASS"
fi

cmp /tmp/hw_32mbytes_sha1 /tmp/sw_32mbytes_sha1
if [ $? -gt 0 ]; then
	echo "SHA1 [OpenSSL] (32 megabytes): FAIL"
else
	echo "SHA1 [OpenSSL] (32 megabytes): PASS"
fi

cmp /tmp/hw_127bytes_md5 /tmp/sw_127bytes_md5
if [ $? -gt 0 ]; then
	echo "MD5 [OpenSSL] (127 bytes): FAIL"
else
	echo "MD5 [OpenSSL] (127 bytes): PASS"
fi

cmp /tmp/hw_128kbytes_md5 /tmp/sw_128kbytes_md5
if [ $? -gt 0 ]; then
	echo "MD5 [OpenSSL] (128 kilobytes): FAIL"
else
	echo "MD5 [OpenSSL] (128 kilobytes): PASS"
fi

cmp /tmp/hw_32mbytes_md5 /tmp/sw_32mbytes_md5
if [ $? -gt 0 ]; then
	echo "MD5 [OpenSSL] (32 megabytes): FAIL"
else
	echo "MD5 [OpenSSL] (32 megabytes): PASS"
fi

echo "Also running tests using /dev/crypto + ioctls (adi-hash.c program):"
echo ""
/crypto/adi-hash
