#!/bin/sh

modprobe cryptodev

echo "Running tests using /dev/crypto + ioctls (adi-crc.c program):"
echo ""
/crypto/adi-crc
