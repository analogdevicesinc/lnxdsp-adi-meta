# Support "deploy dependencies" that must be deployed before this recipe can be fetched;
# effectively the SRC_URI is expected to contain references to ${DEPLOY_DIR_IMAGE}.

# Parse a bunch of things into file://${DEPLOY_DIR_IMAGE}/thing1 for constructing
# SRC_URI from files that we expect to see in the deploy output
def parse_deploy_src (d):
    src = (d.getVar('DEPLOY_SRC_URI') or "").split()
    s = ""
    for elf in src:
        s = s + " file://" + elf
    return s

FILESEXTRAPATHS_prepend = "${DEPLOY_DIR_IMAGE}:"

# Create do_fetch task dependencies on the deploy tasks of the given packages
def create_deploy_depstring (taskname, d):
    tasks = d.getVar(taskname)
    s = ""
    for dep in tasks.split():
        s = s + dep + ":do_deploy "
    return s

do_fetch[depends] += " ${@create_deploy_depstring('DDEPENDS', d)}"
SRC_URI_append = " ${@parse_deploy_src(d)}"
