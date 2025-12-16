DESCRIPTION = "Base device tree package for ZynqMP boards"
LICENSE = "MIT"

inherit dpkg

S = "${STAGING_KERNEL_DIR}"

DPKG_ARCH = "${MACHINE_ARCH}"
PACKAGE_ARCH = "${MACHINE_ARCH}"

do_build() {
    bbnote "Building default DTB with kernel source"
    make ARCH=arm64 CROSS_COMPILE=${TARGET_PREFIX} dtbs -C ${S}
}

do_install() {
    # We do NOT install into ${D}/boot anymore
    install -d ${D}/tmpdtb
    install -m 0644 \
        ${S}/arch/arm64/boot/dts/xilinx/zynqmp-zcu102-rev1.0.dtb \
        ${D}/tmpdtb/devicetree.dtb
}

# Deploy dtb into deploy/images/<machine>
do_deploy_deb:append() {
    bbnote "Deploying DTB into images directory"
    install -Dm 0644 ${D}/tmpdtb/devicetree.dtb \
        ${DEPLOY_DIR_IMAGE}/devicetree.dtb
}

FILES:${PN} += "/tmpdtb/devicetree.dtb"
