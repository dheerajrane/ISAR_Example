DESCRIPTION = "ORAN DHCP client hooks"
LICENSE = "CLOSED"
MAINTAINER = "DHCP configuration <drane@innophaseinc.com>"

inherit dpkg

# Fake upstream source tree
S = "${WORKDIR}/dhclient-hooks-${PV}"

SRC_URI = " \
    file://dhclient.conf \
    file://50-oran-dhcp-info \
"

# ------------------------------------------------------------------
# Create a minimal Debian source tree in ${S}
# ------------------------------------------------------------------
do_prepare_build[dirs] = "${S}"

do_prepare_build() {
    # 1) Create top-level source dir and copy our payload
    mkdir -p ${S}
    cp ${WORKDIR}/dhclient.conf       ${S}/
    cp ${WORKDIR}/50-oran-dhcp-info   ${S}/

    # 2) Debian packaging
    mkdir -p ${S}/debian

    # ---- debian/changelog ----
    cat >${S}/debian/changelog << 'EOF'
dhclient-hooks (1.0-1) unstable; urgency=medium

  * Auto-generated Debian package from ISAR.

 -- DHCP configuration <drane@innophaseinc.com>  Mon, 08 Dec 2025 00:00:00 +0000
EOF

    # ---- debian/control ----
    cat >${S}/debian/control << 'EOF'
Source: dhclient-hooks
Section: misc
Priority: optional
Maintainer: DHCP configuration <drane@innophaseinc.com>
Standards-Version: 4.5.0
Build-Depends: debhelper (>= 11)

Package: dhclient-hooks
Architecture: all
Depends: ${misc:Depends}
Description: ORAN DHCP client hooks
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
	# dhclient.conf -> /usr/etc  (as in your original recipe)
	mkdir -p debian/dhclient-hooks/usr/etc && mkdir -p debian/dhclient-hooks/var/lib/dhcp && mkdir -p debian/dhclient-hooks/var/run
    
	install -m 0644 dhclient.conf debian/dhclient-hooks/usr/etc/

	# dhclient exit hook -> /etc/dhcp/dhclient-exit-hooks.d
	mkdir -p debian/dhclient-hooks/etc/dhcp/dhclient-exit-hooks.d
	install -m 0755 50-oran-dhcp-info \
	    debian/dhclient-hooks/etc/dhcp/dhclient-exit-hooks.d/
EOF

    chmod 0755 ${S}/debian/rules
}

# dpkg handles installation via debian/rules
do_install[noexec] = "1"
