# Select our custom kernel recipe globally
PREFERRED_PROVIDER_virtual/kernel = "linux-xlnx"

# Force kernel version (wildcard allowed)
PREFERRED_VERSION_linux-xlnx = "6.1%"

KERNEL_NAME ?= "xlnx"

# Filenames to be copy into deploy dir
KERNEL_IMAGE = "Image"
#DTB_FILES = "k3-am642-sk.dtb"

# Kernel flavour must match recipe prefix: linux-xlnx_*.bb → flavour = xlnx
LINUX_FLAVOUR ?= "xlnx"

# ISAR-specific: which kernel package name to build
#KERNEL_PACKAGE = "linux-xlnx"

#KERNEL_IMAGE = "vmlinuz-6.1.0-xlnx"
