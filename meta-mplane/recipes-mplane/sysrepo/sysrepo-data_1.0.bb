DESCRIPTION = "Pre-populated sysrepo repository (YANG + data) under /etc/sysrepo"
LICENSE = "CLOSED"
MAINTAINER = "M-Plane Dev <dev@example.com>"

inherit dpkg

# Fake “upstream” source directory
S = "${WORKDIR}/sysrepo-data-${PV}"

SRC_URI = " \
    file://yang/ \
    file://data/ \
"

# We want sysrepo installed when sysrepo-data is pulled in
DEBIAN_DEPENDS = "sysrepo"

# --------------------------------------------------------------
# Prepare minimal Debian source tree in ${S}
# --------------------------------------------------------------
do_prepare_build[dirs] = "${S}"

do_prepare_build() {
    # 1) Create upstream tree and copy payload
    mkdir -p ${S}
    cp -a ${WORKDIR}/yang  ${S}/
    cp -a ${WORKDIR}/data  ${S}/

    # 2) Minimal debian/ packaging
    mkdir -p ${S}/debian

    # ---- debian/changelog ----
    cat >${S}/debian/changelog << 'EOF'
sysrepo-data (1.0-1) unstable; urgency=medium

  * Pre-populated sysrepo repository for ISAR image.

 -- M-Plane Dev <dev@example.com>  Mon, 08 Dec 2025 00:00:00 +0000
EOF

    # ---- debian/control ----
    cat >${S}/debian/control << 'EOF'
Source: sysrepo-data
Section: misc
Priority: optional
Maintainer: M-Plane Dev <dev@example.com>
Standards-Version: 4.5.0
Build-Depends: debhelper (>= 11)

Package: sysrepo-data
Architecture: all
Depends: ${misc:Depends}, sysrepo
Description: Pre-populated sysrepo YANG + datastore under /etc/sysrepo
EOF

    # ---- debian/compat ----
    echo "11" > ${S}/debian/compat

    # ---- debian/rules ----
    cat >${S}/debian/rules << 'EOF'
#!/usr/bin/make -f

%:
	dh $@

override_dh_auto_configure:
	# nothing to configure

override_dh_auto_build:
	# nothing to build

override_dh_auto_install:
	# Install YANG + data into /etc/sysrepo
	mkdir -p debian/sysrepo-data/etc/sysrepo/yang
	mkdir -p debian/sysrepo-data/etc/sysrepo/data

	if [ -d yang ]; then \
		cp -a yang/* debian/sysrepo-data/etc/sysrepo/yang/ || true; \
	fi

	if [ -d data ]; then \
		cp -a data/* debian/sysrepo-data/etc/sysrepo/data/ || true; \
	fi
EOF

    chmod 0755 ${S}/debian/rules
}

# dpkg handles install via debian/rules
do_install[noexec] = "1"
