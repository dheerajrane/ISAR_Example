DESCRIPTION = "Xilinx Linux kernel 6.1"
LICENSE = "GPLv2"

require recipes-kernel/linux/linux-custom.inc

SRC_URI += "git://github.com/Xilinx/linux-xlnx.git;branch=xlnx_rebase_v6.1_LTS_2023.1_update;protocol=https"
SRCREV = "716921b6d7dc9db49660369428fb61ca96947ccb"

PV = "6.1+xlnx"
S = "${WORKDIR}/git"

KERNEL_DEFCONFIG = "xilinx_zynqmp_defconfig"

KERNEL_FILE = "Image"
