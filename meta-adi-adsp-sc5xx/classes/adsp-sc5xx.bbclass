inherit core-image extrausers

SUMMARY = "Minimal image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

ICC = " \
    libmcapi \
    sc5xx-corecontrol \
"

#Not currently compiling for 64 bit -- skip for now
def crypto(d):
  CRYPTO = ""
  MACHINE = d.getVar('MACHINE')
  if MACHINE == 'adsp-sc598-som-ezkit':
    CRYPTO = ""
  else:
    CRYPTO = "cryptodev-module crypto-tests"
  return CRYPTO

CRYPTO = " \
	openssl \
	openssl-bin \
	cryptodev-linux \
	${@crypto(d)} \
"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    packagegroup-base \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    openssh \
    openssl \
    iproute2 \
    iproute2-tc \
    ncurses \
    busybox-watchdog-init \
    util-linux \
    rng-tools \
    spidev-test \
    spitools \
    ${ICC} \
    ${CRYPTO} \
"

IMAGE_INSTALL_append_adsp-sc594-som-ezkit = " \
	remoteproc-examples-sc594 \
"

COMPATIBLE_MACHINE = "(adsp-sc573-ezkit|adsp-sc584-ezkit|adsp-sc589-ezkit|adsp-sc589-mini|adsp-sc594-som-ezkit|adsp-sc598-som-ezkit)"

EXTRA_USERS_PARAMS = " \
	usermod -P adi root; \
"
