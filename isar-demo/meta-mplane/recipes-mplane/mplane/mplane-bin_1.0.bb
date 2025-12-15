DESCRIPTION = "Prebuilt O-RAN m-plane binaries"
MAINTAINER = "M-Plane Dev <dev@example.com>"

inherit dpkg-raw

# 1. Point S to a clean subdirectory, not WORKDIR root
S = "${WORKDIR}/mplane_bin"

# 2. Define dependencies using Isar variables (do not create 'control' manually)
DEBIAN_DEPENDS = "sysrepo, netopeer2"

# 3. Ship all binaries
SRC_URI = "file://mplane_bin/"

do_install() {
    # Install binaries into /opt/mplane
    install -d ${D}/opt/mplane
    
    # Copy files from S (which contains the unpacked SRC_URI)
    # Note: Use -r for recursive copy if mplane_bin has subfolders
    cp -r ${S}/* ${D}/opt/mplane/
}

# 4. Declare package contents (optional in dpkg-raw but good practice)
FILES:${PN} += "/opt/mplane"