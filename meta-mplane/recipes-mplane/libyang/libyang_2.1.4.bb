DESCRIPTION = "libyang2 - YANG parser"
HOMEPAGE = "https://github.com/CESNET/libyang"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fb391cabd4a9d3958ebf8a6c7a36b2e3"

SRC_URI = "git://github.com/CESNET/libyang.git;protocol=https;branch=master"
SRCREV  = "c7e6136c3226f0c6a95d598ff3ab69c8e89b9a40"

S = "${WORKDIR}/git"
PV = "2.1.4"
inherit dpkg

DEBIAN_BUILD_DEPENDS += "cmake, ninja-build, libpcre2-dev, libssl-dev, zlib1g-dev, pkg-config"
DEBIAN_DEPENDS = "libpcre2-8-0, libssl3, zlib1g"

do_prepare_build[cleandirs] += "${S}/debian"
do_prepare_build() {
    deb_debianize

    install -d ${S}/debian/libyang2-dev/usr/include
    install -d ${S}/debian/libyang2-dev/usr/lib

    sed -i '/^Build-Depends:/ s/$/, libpcre2-dev, libssl-dev, zlib1g-dev/' \
        ${S}/debian/control
}
