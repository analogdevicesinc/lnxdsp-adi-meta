GADGET_AUDIO_PATCH = " \
	file://0004-Modifications-for-USB-Audio-support.patch \
	file://feature/cfg/usb_audio.cfg \
"

GADGET_AUDIO_PIO = " \
	file://feature/cfg/usb_audio_pio_only.cfg \
"

SRC_URI:append = "${GADGET_AUDIO_PATCH}"

SRC_URI:append:adsp-sc589-ezkit = "${GADGET_AUDIO_PIO}"
SRC_URI:append:adsp-sc589-mini  = "${GADGET_AUDIO_PIO}"
SRC_URI:append:adsp-sc584-ezkit = "${GADGET_AUDIO_PIO}"
SRC_URI:append:adsp-sc573-ezkit = "${GADGET_AUDIO_PIO}"
