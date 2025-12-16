DESCRIPTION = "Sysrepo YANG datastore"
HOMEPAGE = "https://github.com/sysrepo/sysrepo"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://github.com/sysrepo/sysrepo.git;protocol=https;branch=master \
           file://files/yang/ \
           file://files/data/ \
"
SRCREV = "5fae6f27d5de0f9d7f76cf6953871255a210e978"

S = "${WORKDIR}/git"
PV = "2.2.12"

inherit dpkg

################################################################
# Build dependencies inside Debian schroot (Debian package names)
# (Only *generic* build tools here – we patch libyang/libnetconf2 below)
################################################################
DEBIAN_BUILD_DEPENDS += " \
    cmake, ninja-build, pkg-config, \
    libpcre2-dev, libssh-dev, \
    libssl-dev, zlib1g-dev, libcmocka-dev \
"

################################################################
# Runtime dependencies in the target rootfs
# (use the libs you actually produce)
################################################################
DEBIAN_DEPENDS = " \
    libyang, libnetconf2, \
    libssh-4, libssl3, zlib1g \
"

################################################################
#  Prepare Debian packaging tree and patch debian/control
################################################################
do_prepare_build[cleandirs] += "${S}/debian"

do_prepare_build() {
    # Generate basic Debian packaging (debian/control, rules, etc.)
    deb_debianize

    #
    # 1) Drop upstream’s strict libyang2-dev / libnetconf2-dev versioned deps
    #    and replace them with the packages you *actually* have:
    #    libyang_2.1.4 and libnetconf2_2.1.25
    #
    # If upstream debian/control pulled in something like:
    #   libyang2-dev (>= 2.25.3), libnetconf2-dev (>= 2.1.25)
    # rewrite/remove them.
    #
    sed -i -e 's/libyang2-dev ([^)]*)//g' \
           -e 's/libnetconf2-dev ([^)]*)//g' \
           ${S}/debian/control

    # Clean up possible leftover ", ," or double commas after removal
    sed -i -e 's/,,/,/g' -e 's/, ,/,/g' ${S}/debian/control

    # 2) Explicitly add your custom packages to Build-Depends
    #    (they are in tmp/deploy/isar-apt/... as libyang_2.1.4 and libnetconf2_2.1.25)
    #
    sed -i '/^Build-Depends:/ s/$/, libyang, libnetconf2/' ${S}/debian/control

    #
    # 3) Ensure all the other build tools are listed in Build-Depends too.
    #    (This is mostly belt-and-braces; deb_debianize already put some in.)
    #
    sed -i '/^Build-Depends:/ s/$/, cmake, ninja-build, pkg-config, libpcre2-dev, libssh-dev, libssl-dev, zlib1g-dev, libcmocka-dev/' \
        ${S}/debian/control

    # 3) If upstream Debian packaging has any legacy /etc/sysrepo install
    #    entries, remove them so dh_install does NOT expect those files.
    if [ -f "${S}/debian/sysrepo.install" ]; then
        sed -i '/^etc\/sysrepo\/yang/d'  ${S}/debian/sysrepo.install || true
        sed -i '/^etc\/sysrepo\/data/d'  ${S}/debian/sysrepo.install || true
    fi
    if [ -f "${S}/debian/sysrepo.dirs" ]; then
        sed -i '/^etc\/sysrepo/d' ${S}/debian/sysrepo.dirs || true
    fi

    # (Optional but nice): force default REPO_PATH=/etc/sysrepo in build
    # so that tools look there by default.
    if ! grep -q 'REPO_PATH' ${S}/CMakeLists.txt; then
        cat >> ${S}/CMakeLists.txt << 'EOF'

# Force default repository path for ISAR image
set(REPO_PATH "/etc/sysrepo" CACHE PATH "Sysrepo repository path")
EOF
    fi
}

################################################################
#  No manual do_configure / do_compile / do_install.
#  dpkg-buildpackage (inside sbuild) handles everything.
################################################################
