APP_ABI := all

APP_CPPFLAGS := -std=c++11

# We want API level 16 or higher becasue of position-independent executables (PIE).
# PIE has been supported since API level 16, and later became required since 21.
APP_PLATFORM := android-16

APP_STL := gnustl_static

NDK_TOOLCHAIN_VERSION := 4.9

