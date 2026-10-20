# Wrap run-ptest so files a suite creates in its ptest directory are removed before and
# after each run. The pristine file list is recorded at build time.

ADSP_PTEST_PRISTINE = ".ptest-pristine"

do_install_ptest_base[postfuncs] += "adsp_ptest_cleanup_wrap"

adsp_ptest_cleanup_wrap() {
    cd ${D}${PTEST_PATH}
    [ -f run-ptest ] || return 0

    mv run-ptest run-ptest.real
    cat > run-ptest <<'EOF'
#!/bin/sh
cd "$(dirname "$(readlink -f "$0")")" || exit 1

clean() {
    # Unmount anything a killed suite left mounted here, deepest first, so that rm -rf
    # cannot descend into it.
    awk -v d="$PWD/" 'index($2, d) == 1 { print $2 }' /proc/mounts | sort -r \
        | while IFS= read -r m; do umount "$m" 2>/dev/null || umount -l "$m"; done

    # .debug files belong to the -dbg package, which is split after the list is recorded.
    # Only the top-most new path of each new tree needs removing.
    find . -mindepth 1 | grep -vxF -f ${ADSP_PTEST_PRISTINE} \
        | grep -Ev '/\.debug(-static)?(/|$)' | sort \
        | awk 'p == "" || index($0, p "/") != 1 { print; p = $0 }' \
        | while IFS= read -r f; do rm -rf -- "$f"; done
    }

clean
trap clean EXIT
trap 'exit 129' HUP
trap 'exit 143' TERM
trap 'exit 130' INT

./run-ptest.real "$@"
EOF
    chmod 0755 run-ptest
}

# Recorded at package time so files added after do_install (e.g. ptest/bin alternatives
# symlinks from ptest_update_alternatives) count as pristine.
PACKAGE_PREPROCESS_FUNCS:append = "${@bb.utils.contains('PTEST_ENABLED', '1', ' adsp_ptest_pristine_list', '', d)}"

adsp_ptest_pristine_list() {
    [ -f ${PKGD}${PTEST_PATH}/run-ptest.real ] || return 0
    cd ${PKGD}${PTEST_PATH}
    # The list includes itself, and is sorted so that the package is reproducible.
    touch ${ADSP_PTEST_PRISTINE}
    find . -mindepth 1 | LC_ALL=C sort -o ${ADSP_PTEST_PRISTINE}
}
