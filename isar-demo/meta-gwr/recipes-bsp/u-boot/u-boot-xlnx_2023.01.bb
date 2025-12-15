DESCRIPTION = "Xilinx Linux Uboot 2023.01"
LICENSE = "GPLv2"

HOST_EXTRACFLAGS='-I/usr/include -I/usr/include/x86_64-linux-gnu'

require recipes-bsp/u-boot/u-boot-custom.inc

HOST_EXTRACFLAGS += " -I/usr/include/x86_64-linux-gnu"
export HOST_EXTRACFLAGS
# Install those packages into buildchroot before build
#DEBIAN_BUILD_DEPENDS = "bc, \
#                        bison, \
#                        flex, \
#                        device-tree-compiler, \
#                        pkg-config \
#                        "

DEBIAN_BUILD_DEPENDS += ", libssl-dev:native, uuid-dev:native, libuuid1:native, libgnutls28-dev:native, libgnutls30:native ,libgnutls28-dev, libgnutls30, uuid-dev, libuuid1"

#DEBIAN_BUILD_DEPENDS += ", libssl-dev:native, libgnutls28-dev:native, libuuid-dev:native, libuuid1:native, libgnutls30:native, pkg-config"
DEBIAN_BUILD_DEPENDS += "${@', libssl-dev, libgnutls30, libuuid1' if d.getVar('ISAR_CROSS_COMPILE') == '1' else ''}"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "git://github.com/Xilinx/u-boot-xlnx.git;branch=xlnx_rebase_v2023.01;protocol=https"

#SRC_URI += " file://disable-signing.cfg"
UBOOT_CONFIG_FRAGMENTS:append = " disable-ssl.cfg"
SRCREV = "40a08d69e749c0472103551c85c02c41f979453d"

PV = "2023.1+xlnx"
S = "${WORKDIR}/git"

U_BOOT_CONFIG = "xilinx_zynqmp_virt_defconfig"

U_BOOT_BIN = "u-boot.elf"

do_deploy_deb:append() {
    echo "Extracting U-Boot ELF from deb package..."

    DEBFILE=$(ls ${WORKDIR}/u-boot-z19-arm64-jammy*_arm64.deb | head -1)
    echo "DEBFILE=${DEBFILE}"

    TMPDIR=$(mktemp -d /tmp/ubootXXXXXX)
    dpkg -x "${DEBFILE}" "${TMPDIR}"

    UBOOT_ELF=$(find ${TMPDIR}/usr/lib/u-boot -type f -name u-boot.elf | head -1)
    echo "FOUND UBOOT_ELF=${UBOOT_ELF}"

    if [ -f "${UBOOT_ELF}" ]; then
        #install -Dm0644 "${UBOOT_ELF}" "${DEPLOY_DIR}/u-boot.elf"
        install -Dm0644 "${UBOOT_ELF}" "${DEPLOY_DIR_IMAGE}/u-boot.elf"
        echo "u-boot.elf copied to ${DEPLOY_DIR}"
    else
        echo "ERROR: u-boot.elf not found inside deb package! Printing contents..."
        find ${TMPDIR}
    fi

    rm -rf "${TMPDIR:?}"
}

