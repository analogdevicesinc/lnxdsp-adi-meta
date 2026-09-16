# Same DWARF directory-entry-0 leak as the readline bbappend in this layer:
# static libraries keep their debug info (package.py routes .a files down the
# checkstatic path that skips the debug-split), so the build dir stays visible
# in libcheck.a.
#
# Scoped to -staticdev on purpose - see recipes-core/readline for the full
# explanation of why the package-wide pn- form is the wrong tool here.
INSANE_SKIP:${PN}-staticdev += "buildpaths"
