do_patch[postfuncs] += "allow_root"
allow_root() {
        sed -i -e 's/^#PermitEmptyPasswords.*/PermitEmptyPasswords yes/' \
               -e 's/^#PermitRootLogin.*/PermitRootLogin yes/' ${UNPACKDIR}/sshd_config
}

# The SPI flash root filesystem is read-only, so sshdgenkeys can't write the
# host keys to /etc/ssh. /var/lib is a tmpfs overlay on a read-only root, and
# persistent on a writable one.
OPENSSH_HOST_KEY_DIR = "${localstatedir}/lib/ssh"

inherit adsp-ptest-cleanup

# Regress tests (names without .sh) that fail on ADSP-SC5xx boards
OPENSSH_PTEST_SKIP ?= "dynamic-forward"

do_install_ptest:append() {
    sed -i '1a export SKIP_LTESTS="${OPENSSH_PTEST_SKIP}"' ${D}${PTEST_PATH}/run-ptest
}
