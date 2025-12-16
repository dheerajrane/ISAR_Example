FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Custom board-specific DTS and DTSI files
SRC_URI += "file://zynqmp-z19-custom.dts"

do_build:prepend() {
    bbnote "Copying custom DTS & DTSI and building final board DTB"

    # Copy custom DTS and DTSI into the kernel DTS tree
    install -m 0644 ${WORKDIR}/zynqmp-z19-custom.dts \
        ${S}/arch/arm64/boot/dts/xilinx/zynqmp-z19-custom.dts

    # Build only the custom board DTB
    make ARCH=arm64 CROSS_COMPILE=${TARGET_PREFIX} \
        xilinx/zynqmp-z19-custom.dtb -C ${S}
}

do_install:prepend() {
    bbnote "Installing custom DTB (replacing vendor DTB)"
    install -m 0644 \
        ${S}/arch/arm64/boot/dts/xilinx/zynqmp-z19-custom.dtb \
        ${D}/tmpdtb/devicetree.dtb
}

do_deploy_deb:append() {
    bbnote "Deploying custom DTB to images folder"
    install -Dm0644 ${D}/tmpdtb/devicetree.dtb \
        ${DEPLOY_DIR_IMAGE}/devicetree.dtb
}
