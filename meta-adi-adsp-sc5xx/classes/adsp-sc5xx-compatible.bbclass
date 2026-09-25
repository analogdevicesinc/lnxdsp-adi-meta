ADI_SC57X = "adsp-sc573-ezlite"
ADI_SC58X = "adsp-sc584-ezkit|adsp-sc589-ezkit|adsp-sc589-mini"
ADI_SC594 = "adsp-sc594-som-ezkit|adsp-sc594-som-ezlite"
ADI_SC598 = "adsp-sc598-som-ezkit|adsp-sc598-som-ezlite"
ADI_SC846 = "adsp-sc846-som-ezkit"

COMPATIBLE_MACHINE = "(${ADI_SC57X}|${ADI_SC58X}|${ADI_SC594}|${ADI_SC598}|${ADI_SC846})"

MUSL_ALLOWED_IMAGES = "adsp-sc5xx-tiny adsp-sc5xx-ramdisk"
python () {
    if (bb.data.inherits_class('image', d)
            and d.getVar('DISTRO') == 'adi-distro-musl'
            and d.getVar('PN') not in d.getVar('MUSL_ALLOWED_IMAGES').split()):
        raise bb.parse.SkipRecipe(
            "adi-distro-musl only supports the adsp-sc5xx-tiny image; "
            "build %s with adi-distro-glibc or adi-security instead."
            % d.getVar('PN'))
}
