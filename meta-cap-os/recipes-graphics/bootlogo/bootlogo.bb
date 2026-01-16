SUMMARY = "Framebuffer boot logo"
LICENSE = "CLOSED"

SRC_URI = " \ 
file://bootlogo.rgb \ 
file://bootlogo.sh \ 
file://bootlogo.service\
"

S = "${WORKDIR}"

inherit systemd

# Systemd Settings
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "bootlogo.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() { 
# Creating directories in rootfs 
install -d ${D}${bindir} 
install -d ${D}${datadir}/bootlogo 
install -d ${D}${systemd_system_unitdir} 

# Installing the files 
install -m 0755 ${S}/bootlogo.sh ${D}${bindir}/bootlogo.sh 
install -m 0644 ${S}/bootlogo.rgb ${D}${datadir}/bootlogo/bootlogo.rgb 
install -m 0644 ${S}/bootlogo.service ${D}${systemd_system_unitdir}/bootlogo.service
}

# At the end of your bootlogo.bb
FILES:${PN} += "${bindir}/bootlogo.sh"
FILES:${PN} += "${datadir}/bootlogo/bootlogo.rgb"
FILES:${PN} += "${systemd_system_unitdir}/bootlogo.service"