DESCRIPTION = "GWR Image using package list"
LICENSE = "MIT"

require recipes-core/images/isar-image-base.bb

# Include version pinning for packages
include conf/distro/include/gwr-ubuntu-jammy.inc

IMAGER_BUILD_DEPS += "u-boot-xlnx"
IMAGE_INSTALL += " linux-image-xlnx"
IMAGE_INSTALL:remove = "linux-image-generic linux-image-arm64 linux-image-*"

# Remove Debian kernel packages
BAD_RECOMMENDATIONS += "linux-image-generic linux-image-arm64 linux-image-5.15*"
PACKAGE_EXCLUDE += "linux-image-generic linux-image-arm64 linux-image-5.15*"

IMAGE_INSTALL:append = " sshd-regen-keys"

IMAGE_PREINSTALL:append = " \
    busybox-static \
    ethtool \
    iptables \
    vim \
    net-tools \
    ca-certificates \
    curl \
    fdisk \
    file \
    gawk \
    i2c-tools \
    iproute2 \
    iptables \
    iputils-ping \
    isc-dhcp-client \
    isc-dhcp-common \
    openssl \
    openssh-sftp-server \
    openssh-server \
    less \
    lsb-release \
    python3 \
    rsyslog \
    tcpdump \
    time \
    tzdata \
    unzip \
    zip \
"

# Xilinx packages support (Jammy PPA)
UBUNTU_APT_SOURCES += " \
    deb [trusted=yes] http://ppa.launchpad.net/xilinx-apps/ubuntu jammy main \
"

#Xlinx packages
IMAGE_PREINSTALL:append = " \
    fpga-manager-xlnx \
"
