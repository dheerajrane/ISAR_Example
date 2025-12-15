DESCRIPTION = "Startup scripts for m-plane & netopeer2"
LICENSE = "CLOSED"
MAINTAINER = "M-Plane Dev <dev@example.com>"

inherit dpkg

# Upstream “source tree” we fake for dpkg
S = "${WORKDIR}/mplane-start-${PV}"

# Our payload
SRC_URI = " \
    file://start-mplane.sh \
    file://ethernet.txt \
    file://mplane.service \
"

# ------------------------------------------------------------------
# Prepare a minimal Debian source tree in ${S}
# ------------------------------------------------------------------
do_prepare_build[dirs] = "${S}"

do_prepare_build() {
    # 1) Create "upstream" source dir and copy our payload
    mkdir -p ${S}
    cp ${WORKDIR}/start-mplane.sh  ${S}/
    cp ${WORKDIR}/ethernet.txt     ${S}/
    cp ${WORKDIR}/mplane.service   ${S}/

    # 2) Minimal debian/ packaging
    mkdir -p ${S}/debian

    # ---- debian/changelog ----
    cat >${S}/debian/changelog << 'EOF'
mplane-start (1.0-1) unstable; urgency=medium

  * Auto-generated Debian package from ISAR.

 -- M-Plane Dev <dev@example.com>  Mon, 08 Dec 2025 00:00:00 +0000
EOF

    # ---- debian/control ----
    cat >${S}/debian/control << 'EOF'
Source: mplane-start
Section: misc
Priority: optional
Maintainer: M-Plane Dev <dev@example.com>
Standards-Version: 4.5.0
Build-Depends: debhelper (>= 11)

Package: mplane-start
Architecture: all
Depends: ${misc:Depends}, ${shlibs:Depends}, sysrepo, netopeer2
Description: Startup scripts for m-plane & netopeer2
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
	# install start-mplane.sh into /usr/bin (NOT /usr/local)
	mkdir -p debian/mplane-start/usr/bin
	install -m 0755 start-mplane.sh debian/mplane-start/usr/bin/

	# install ethernet.txt
	mkdir -p debian/mplane-start/run/media/mmcblk0p2/np2
	install -m 0644 ethernet.txt debian/mplane-start/run/media/mmcblk0p2/np2/

	# install systemd service
	mkdir -p debian/mplane-start/lib/systemd/system
	install -m 0644 mplane.service debian/mplane-start/lib/systemd/system/

override_dh_systemd_enable:
	dh_systemd_enable mplane.service

override_dh_systemd_start:
	# don't start in chroot, just enable on target
	dh_systemd_start --no-start mplane.service
EOF

    chmod 0755 ${S}/debian/rules

    # ---- debian/mplane-start.postinst ----
    cat >${S}/debian/mplane-start.postinst << 'EOF'
#!/bin/sh
set -e

case "$1" in
    configure)
        if command -v systemctl >/dev/null 2>&1; then
            systemctl enable mplane.service || true
        fi
    ;;
esac

exit 0
EOF
    chmod 0755 ${S}/debian/mplane-start.postinst
}

# dpkg handles install via debian/rules
do_install[noexec] = "1"
