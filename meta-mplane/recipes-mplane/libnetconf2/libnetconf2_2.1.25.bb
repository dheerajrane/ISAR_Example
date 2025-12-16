DESCRIPTION = "libnetconf2 - NETCONF protocol library"
HOMEPAGE = "https://github.com/CESNET/libnetconf2"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6f4b002e789e0dfbbfbb8e65d91f75f6"

SRC_URI = "git://github.com/CESNET/libnetconf2.git;protocol=https;branch=master"
SRCREV = "74bf203f085d2c67d78d9647fe2abef9ce9ed4c8"

S = "${WORKDIR}/git"
PV = "2.1.25"

###############################################################
#  Use Debian-style build (same method as libyang)
###############################################################
inherit dpkg

BBCLASSEXTEND = ""
###############################################################
#  Debian build dependencies used inside the schroot
###############################################################
DEBIAN_BUILD_DEPENDS += " \
    cmake, ninja-build, pkg-config, \
    libyang, libpcre2-dev, libssh-dev, \
    libssl-dev, zlib1g-dev, libcurl4-gnutls-dev \
"

###############################################################
#  Runtime dependencies (inside target rootfs)
###############################################################
DEBIAN_DEPENDS = " \
    libyang, \
    libpcre2-8-0, libssh-4, libssl3, \
    zlib1g, libcurl3-gnutls \
"

###############################################################
#  Prepare Debian packaging tree
###############################################################
do_prepare_build[cleandirs] += "${S}/debian"

do_prepare_build() {
    deb_debianize

    # Add override dependencies into debian/control
    sed -i '/^Build-Depends:/ s/$/, libyang/' ${S}/debian/control
}

###############################################################
#  No manual do_compile/do_install.
#  dpkg-buildpackage handles everything.
###############################################################
