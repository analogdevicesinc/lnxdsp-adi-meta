inherit adsp-sc5xx

SUMMARY = "Full image for Analog Devices ADSP-SC5xx boards"
LICENSE = "MIT"

do_image_wic[depends] += " \
        ${IMAGE_BASENAME}:do_image_ext4 \
"

OVERRIDES:append = ":adsp-demo-image"


FILE_SYSTEM_TOOLS = "\
    e2fsprogs \
"

TESTING = "\
    sram-mmap-test \
"

SOUND = " \
    alsa-utils \
    alsa-lib \
    rpmsg-utils \
    dbus \
    play \
"

UTILS = " \
    iperf3 \
    netperf \
    cpufrequtils \
    uftrace \
    ltrace \
    strace \
    bonnie++ \
"

JUPYTER = " \
    python3-jupyter \
    python3-jupyterlab \
    python3-jupyter-server \
    python3-websocket-client \
    python3-notebook-shim \
    python3-nbconvert \
    python3-nbclient \
    python3-nest-asyncio \
    python3-psutil \
    python3-jupyterlab-pygments \
"

UTILS += "${@'' if (bb.utils.to_boolean(d.getVar('ADSP_KERNEL_TYPE') == 'upstream')) else ' perf '}"

IMAGE_INSTALL += " \
    ${UTILS} \
       ${FILE_SYSTEM_TOOLS} \
       ${TESTING} \
    ${SOUND} \
    ltp \
    linuxptp \
    linux-firmware-rtl8192su \
    linux-firmware-adau1761 \
    mtd-utils \
    mtd-utils-ubifs \
    version \
    libopus \
    opus-tools \
    python3-pyrpmsg \
    ${JUPYTER} \
    neofetch \
"
