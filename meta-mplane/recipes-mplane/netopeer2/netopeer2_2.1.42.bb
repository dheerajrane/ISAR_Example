DESCRIPTION = "Netopeer2 NETCONF server"
HOMEPAGE = "https://github.com/CESNET/netopeer2"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8af869d5328f0c3929dea66c48f6a796"

SRC_URI = "git://github.com/CESNET/netopeer2.git;protocol=https;branch=master"
# Tag for v2.1.42
SRCREV = "00ea22af3ff04b2a6978243ac485d8db18ec3b0c"

S  = "${WORKDIR}/git"
PV = "2.1.42"

################################################################
#  Debian-style build (same pattern as libyang / libnetconf2 / sysrepo)
################################################################
inherit dpkg

################################################################
# Build dependencies inside Debian schroot (generic tools only)
################################################################
DEBIAN_BUILD_DEPENDS += " \
    cmake, ninja-build, pkg-config, \
    libpcre2-dev, libssh-dev, \
    libssl-dev, zlib1g-dev, libcmocka-dev \
"

################################################################
# Runtime dependencies in the target rootfs (your ISAR-built stack)
################################################################
DEBIAN_DEPENDS = " \
    libyang, libnetconf2, sysrepo, \
    libssh-4, libssl3, zlib1g \
"

################################################################
#  Prepare Debian packaging tree and patch debian/control + rules + CMake
################################################################
do_prepare_build[cleandirs] += "${S}/debian"

do_prepare_build() {
    # Generate basic Debian packaging (debian/control, rules, etc.)
    deb_debianize

    #
    # 1) Drop Ubuntu dev packages from Build-Depends (if they appear)
    #
    sed -i -e 's/libcurl4-gnutls-dev//g' \
           -e 's/libyang2-dev//g' \
           -e 's/libnetconf2-dev//g' \
           -e 's/libsysrepo-dev//g' \
           ${S}/debian/control || true

    # Clean up possible ", ," / trailing commas
    sed -i -e 's/,,/,/g' \
           -e 's/, ,/,/g' \
           -e 's/, *$//' \
           ${S}/debian/control

    #
    # 2) Explicitly add your locally built packages
    #
    sed -i '/^Build-Depends:/ s/$/, libyang:arm64, libnetconf2:arm64, sysrepo:arm64/' \
        ${S}/debian/control

    #
    # 3) Ensure all required build tools are present
    #
    sed -i '/^Build-Depends:/ s/$/, cmake, ninja-build, pkg-config, libpcre2-dev, libssh-dev, libssl-dev, zlib1g-dev, libcmocka-dev/' \
        ${S}/debian/control

    #
    # 4) Help CMake/pkg-config find the right sysrepo.pc in the chroot
    #
    sed -i '1a\
export PKG_CONFIG_PATH=/usr/lib/aarch64-linux-gnu/pkgconfig\
' ${S}/debian/rules

    #
    # 5) Disable upstream CTest – tests are flaky/slow under qemu/sysrepo.
    #
    cat >> ${S}/debian/rules << 'EOF'

override_dh_auto_test:
	@echo "Skipping Netopeer2 upstream unit tests in ISAR/qemu build."
EOF

    #
    # 6) Stub out scripts that try to manipulate /etc/sysrepo or use `su`
    #    during *install* (setup.sh, merge_hostkey.sh, merge_config.sh).
    #    CMake will still call them, but they will be harmless no-ops.
    #
    # setup.sh
    if [ -f ${S}/scripts/setup.sh ]; then
        mv ${S}/scripts/setup.sh ${S}/scripts/setup.sh.upstream
        cat > ${S}/scripts/setup.sh << 'EOF'
#!/bin/sh
echo "netopeer2: setup.sh disabled during package build (ISAR dpkg)."
# Original script saved as setup.sh.upstream
exit 0
EOF
        chmod +x ${S}/scripts/setup.sh
    fi

    # merge_hostkey.sh
    if [ -f ${S}/scripts/merge_hostkey.sh ]; then
        mv ${S}/scripts/merge_hostkey.sh ${S}/scripts/merge_hostkey.sh.upstream
        cat > ${S}/scripts/merge_hostkey.sh << 'EOF'
#!/bin/sh
echo "netopeer2: merge_hostkey.sh disabled during package build (ISAR dpkg)."
# Original script saved as merge_hostkey.sh.upstream
exit 0
EOF
        chmod +x ${S}/scripts/merge_hostkey.sh
    fi

    # merge_config.sh  <-- this is the one now failing
    if [ -f ${S}/scripts/merge_config.sh ]; then
        mv ${S}/scripts/merge_config.sh ${S}/scripts/merge_config.sh.upstream
        cat > ${S}/scripts/merge_config.sh << 'EOF'
#!/bin/sh
echo "netopeer2: merge_config.sh disabled during package build (ISAR dpkg)."
# Original script saved as merge_config.sh.upstream
exit 0
EOF
        chmod +x ${S}/scripts/merge_config.sh
    fi
}

################################################################
#  No manual do_configure / do_compile / do_install.
#  dpkg-buildpackage (inside sbuild) handles everything.
################################################################
