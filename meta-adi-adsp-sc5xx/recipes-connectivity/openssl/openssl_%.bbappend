# devcrypto engine routes openssl through cryptodev to the kernel crypto drivers;
# legacy provides the DES software reference used by crypto-tests
PACKAGECONFIG:append:class-target = " cryptodev-linux legacy"
