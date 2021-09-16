require linux-adi.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

PR = "r0"

def get_kernel_branch(d):
  BRANCH = ""
  MACHINE = d.getVar('MACHINE')
  if MACHINE == 'adsp-sc598-som-ezkit':
    BRANCH = "develop/a55-fpga"
  else:
    BRANCH = "v5.4-rebase-wip"
  return BRANCH

KERNEL_BRANCH ?= "${@get_kernel_branch(d)}"
SRCREV  = "${AUTOREV}"

SRC_URI += "file://feature/"

# Include kernel configuration fragment
KERNEL_EXTRA_FEATURES ?= "${WORKDIR}/feature/cfg/nfs.cfg \
						  ${WORKDIR}/feature/cfg/wireless.cfg \
						  ${WORKDIR}/feature/cfg/cpufreq.cfg \
						  ${WORKDIR}/feature/cfg/crypto.cfg \
						  "
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"

KERNEL_FEATURES_append_adsp-sc594_som_ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc589-ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc584-ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc573-ezkit = " ${WORKDIR}/feature/snd_ezkit.scc"
KERNEL_FEATURES_append_adsp-sc589-mini = " ${WORKDIR}/feature/snd_mini.scc"

do_install_append(){
	rm -rf ${D}/lib/modules/5.4.0-yocto-standard/modules.builtin.modinfo
}
