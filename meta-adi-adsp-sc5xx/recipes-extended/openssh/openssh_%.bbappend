do_patch[postfuncs] += "allow_root"
allow_root() {
        sed -i -e 's/^#PermitEmptyPasswords.*/PermitEmptyPasswords yes/' \
               -e 's/^#PermitRootLogin.*/PermitRootLogin yes/' ${UNPACKDIR}/sshd_config
}

# Regress tests (names without .sh) that fail on ADSP-SC5xx boards
OPENSSH_PTEST_SKIP ?= "dynamic-forward"

do_install_ptest:append() {
    sed -i '1a export SKIP_LTESTS="${OPENSSH_PTEST_SKIP}"' ${D}${PTEST_PATH}/run-ptest
}
