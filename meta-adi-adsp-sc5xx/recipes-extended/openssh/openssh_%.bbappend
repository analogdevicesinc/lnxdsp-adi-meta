do_patch[postfuncs] += "allow_root"
allow_root() {
        sed -i -e 's/^#PermitEmptyPasswords.*/PermitEmptyPasswords yes/' \
               -e 's/^#PermitRootLogin.*/PermitRootLogin yes/' ${WORKDIR}/sshd_config
}
