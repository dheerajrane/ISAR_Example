# This software is a part of isar demo.
# Copyright (c) Siemens AG, 2025
#
# SPDX-License-Identifier: MIT

#require recipes-core/images/isar-image-base.bb
require recipes-core/images/gwr-image.bb

#IMAGER_BUILD_DEPS += "u-boot-xlnx"
#IMAGE_INSTALL += " linux-image-xlnx"
#IMAGE_INSTALL:remove = "linux-image-generic linux-image-arm64 linux-image-*" 
IMAGE_INSTALL += " custom-app"

# Remove Debian kernel packages
#BAD_RECOMMENDATIONS += "linux-image-generic linux-image-arm64 linux-image-5.15*"
#PACKAGE_EXCLUDE += "linux-image-generic linux-image-arm64 linux-image-5.15*"

USERS += "root"
USER_root[flags] = "clear-text-password"
USER_root[password] = "root"
