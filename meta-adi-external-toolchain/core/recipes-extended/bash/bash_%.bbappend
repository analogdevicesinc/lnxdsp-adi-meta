do_compile_append () {
    if [ -e support/bash.pc ] ; then
        sed -i -e 's#-B${gcc_bindir}##' support/bash.pc
    fi
}
