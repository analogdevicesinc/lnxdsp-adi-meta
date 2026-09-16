# Same DWARF directory-entry-0 leak as the readline bbappend in this layer:
# static libraries keep their debug info, so ${B} stays visible in the C++
# archives libncurses++.a and libncurses++w.a.
#
# Scoped to -staticdev on purpose - see recipes-core/readline for the full
# explanation of why the package-wide pn- form is the wrong tool here.
INSANE_SKIP:${PN}-staticdev += "buildpaths"
