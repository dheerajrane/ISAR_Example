DESCRIPTION = "Enable root SSH login and passwords"
LICENSE = "CLOSED"
MAINTAINER = "SSH config <dev@example.com>"

inherit dpkg

# Minimal "upstream" tree
S = "${WORKDIR}/sshd-config-${PV}"

SRC_URI = ""

do_prepare_build[dirs] = "${S}"

do_prepare_build() {
    mkdir -p ${S}
    # Optional dummy file to make dpkg-source happy
    echo "sshd configuration helper package" > ${S}/README

    mkdir -p ${S}/debian

    # ---- debian/changelog ----
    cat >${S}/debian/changelog << 'EOF'
sshd-config (1.0-1) unstable; urgency=medium

  * Auto-generated Debian package from ISAR.

 -- SSH config <dev@example.com>  Mon, 08 Dec 2025 00:00:00 +0000
EOF

    # ---- debian/control ----
    cat >${S}/debian/control << 'EOF'
Source: sshd-config
Section: misc
Priority: optional
Maintainer: SSH config <dev@example.com>
Standards-Version: 4.5.0
Build-Depends: debhelper (>= 11)

Package: sshd-config
Architecture: all
Depends: ${misc:Depends}, openssh-server
Description: Enable root SSH login and password authentication
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
	# no files needed; behavior is in postinst
	mkdir -p debian/sshd-config/usr/share/doc/sshd-config
	install -m 0644 README debian/sshd-config/usr/share/doc/sshd-config/
EOF

    chmod 0755 ${S}/debian/rules

    # ---- debian/sshd-config.postinst ----
    cat >${S}/debian/sshd-config.postinst << 'EOF'
#!/bin/sh
set -e

case "$1" in
    configure)
        # Ensure directory exists
        if [ ! -d /etc/ssh ]; then
            mkdir -p /etc/ssh
        fi

        if [ -f /etc/ssh/sshd_config ]; then
            # Update existing directives (or uncomment them)
            sed -i \
                -e 's/^[# ]*PermitRootLogin .*/PermitRootLogin yes/' \
                -e 's/^[# ]*PasswordAuthentication .*/PasswordAuthentication yes/' \
                /etc/ssh/sshd_config || true
        else
            # Create a minimal config if none exists
            {
                echo "PermitRootLogin yes"
                echo "PasswordAuthentication yes"
            } > /etc/ssh/sshd_config
        fi

        # Try to restart SSH, but don't fail hard if it doesn't exist
        if command -v systemctl >/dev/null 2>&1; then
            systemctl restart ssh 2>/dev/null || systemctl restart sshd 2>/dev/null || true
        fi
    ;;
esac

exit 0
EOF
    chmod 0755 ${S}/debian/sshd-config.postinst
}

# dpkg handles install via debian/rules
do_install[noexec] = "1"
