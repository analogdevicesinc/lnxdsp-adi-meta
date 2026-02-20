# Create do_fetch task dependencies on the deploy tasks of the given packages
def create_deploy_depstring (taskname, d):
    tasks = d.getVar(taskname)
    s = ""
    for dep in tasks.split():
        s = s + dep + ":do_deploy "
    return s

do_compile[depends] += " ${@create_deploy_depstring('DDEPENDS', d)}"
