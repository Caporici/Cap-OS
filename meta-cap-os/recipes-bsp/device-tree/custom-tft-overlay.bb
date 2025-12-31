SUMMARY = "Custom Device Tree overlay for 3.5 TFT"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://tft35a.dtbo"

S = "${WORKDIR}"

inherit deploy

do_install() {
    install -d ${D}/boot/overlays
    install -m 0644 ${WORKDIR}/tft35a.dtbo ${D}/boot/overlays/
}

do_deploy() {
    install -d ${DEPLOYDIR}
    # Copia o arquivo para a raiz do deploy com o nome exato que o erro pede
    install -m 0644 ${WORKDIR}/tft35a.dtbo ${DEPLOYDIR}/tft35a.dtbo
}

# Isso garante que a pasta de deploy esteja limpa antes de começar
do_deploy[cleandirs] = "${DEPLOYDIR}"
addtask deploy after do_install before do_build

# ESTA É A LINHA QUE FALTA:
FILES:${PN} = "/boot/overlays/tft35a.dtbo"
