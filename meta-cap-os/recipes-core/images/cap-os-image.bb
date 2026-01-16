require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL += " \
    dhcpcd \
    openssh \
    bootlogo \
    kernel-module-fbtft \
    kernel-module-fb-ili9486 \
    custom-tft-overlay \
"
