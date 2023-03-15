inherit core-image extrausers adsp-sc5xx-compatible

SHARC_ALSA_BINARIES = "${@bb.utils.contains_any('DISTRO_FEATURES', 'adi_sharc_alsa_audio', 'sharc-audio', '', d)}"
HYBRID_BINARIES = "${@bb.utils.contains_any('DISTRO_FEATURES', 'adi_hybrid_audio', 'hybrid-audio', '', d)}"
LINUX_ONLY_BINARIES = "${@bb.utils.contains_any('DISTRO_FEATURES', 'linux_only_audio', 'rpmsg-echo-example', '', d)}"

IMAGE_INSTALL = " \
    busybox-watchdog-init \
    initramfs-init \
    busybox \
    ${SHARC_ALSA_BINARIES} \
    ${HYBRID_BINARIES} \
    ${LINUX_ONLY_BINARIES} \
"

DISTRO_FEATURES += " ram"
IMAGE_FSTYPES = " cpio.xz cpio.gz"

DEPENDS += "u-boot-tools-native"

#We do not need these files in the rootfs -- remove them to reduce the minimal rootfs size
fakeroot do_rootfs_cleanup(){
	rm -rf ${IMAGE_ROOTFS}/usr/lib/opkg
	rm -rf ${IMAGE_ROOTFS}/usr/lib/locale
	rm -rf ${IMAGE_ROOTFS}/sbin/ldconfig
	rm -rf ${IMAGE_ROOTFS}/etc/ssh/moduli
	rm -rf ${IMAGE_ROOTFS}/usr/sbin/useradd
	rm -rf ${IMAGE_ROOTFS}/usr/sbin/userdel
	rm -rf ${IMAGE_ROOTFS}/usr/sbin/usermod
}

addtask rootfs_cleanup after do_rootfs before do_image

# printf "%q" $(mkpasswd -m sha256crypt adi)
PASSWD_ROOT = "\$5\$j9T8zDE13LXUGyc6\$utDvGwFWR.kt/AKwwbHnXC14HJBqbcWwvLoDDLMQrc8"
EXTRA_USERS_PARAMS = "usermod -p '${PASSWD_ROOT}' root;"

python __anonymous() {
    count=0
    if bb.utils.contains('DISTRO_FEATURES', 'adi_sharc_alsa_audio', True, False, d):
        count=count+1
    if bb.utils.contains('DISTRO_FEATURES', 'adi_sharc_alsa_audio_uboot', True, False, d):
        count=count+1
    if bb.utils.contains('DISTRO_FEATURES', 'adi_hybrid_audio', True, False, d):
        count=count+1
    if bb.utils.contains('DISTRO_FEATURES', 'linux_only_audio', True, False, d):
        count=count+1

    if count > 1:
        bb.fatal("You have multiple audio modes in DISTRO_FEATURES. \
        Choose only one of: adi_sharc_alsa_audio, adi_sharc_alsa_audio_uboot, adi_hybrid_audio, linux_only_audio")

    if count == 0:
        bb.fatal("You need to select your audio mode in DISTRO_FEATURES. \
        Choose only one of: adi_sharc_alsa_audio, adi_sharc_alsa_audio_uboot, adi_hybrid_audio, linux_only_audio")
}
