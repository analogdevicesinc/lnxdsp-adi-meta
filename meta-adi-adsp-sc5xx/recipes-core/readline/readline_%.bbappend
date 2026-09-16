# DWARF line-table directory entry 0 is the compilation directory. DWARF 5
# requires it to hold the real cwd, and GCC deliberately does not rewrite it
# under -ffile-prefix-map because debuggers need it to resolve relative paths.
#
# Shared libraries are unaffected: their .debug_* sections are split out into
# -dbg and the remainder is stripped. Static libraries are not - package.py
# routes .a files down a separate checkstatic path that skips the debug-split,
# so ${B} stays visible in libreadline.a and libhistory.a.
#
# Scoped to -staticdev on purpose. The package-wide pn- form would also stop
# checking -dbg, which is where the genuine LTO path leak was caught.
INSANE_SKIP:${PN}-staticdev += "buildpaths"
