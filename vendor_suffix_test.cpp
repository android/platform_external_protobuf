#include <link.h>

#include <string>
#include <vector>

#include <android-base/stringprintf.h>

#include <gtest/gtest.h>

#include <google/protobuf/message_lite.h>

TEST(vendor_suffix, suffix) {
    std::vector<std::string> libs;
    dl_iterate_phdr([](dl_phdr_info* info, size_t, void* data) -> int {
        auto local_libs = static_cast<decltype(&libs)>(data);
        std::string name = info->dlpi_name;
        if (name.starts_with("libprotobuf-cpp")) {
            local_libs->push_back(name);
        }
        return 0;
    }, &libs);

    std::sort(libs.begin(), libs.end());

    std::string version = android::base::StringPrintf("-%d.%d.%d",
       GOOGLE_PROTOBUF_VERSION / 1000000,
       GOOGLE_PROTOBUF_VERSION / 1000 % 1000,
       GOOGLE_PROTOBUF_VERSION % 1000);

    std::string suffix = GOOGLE_PROTOBUF_VERSION_SUFFIX;
    if (suffix != "") {
        version += "-" + suffix;
    }

    std::vector<std::string> expect = {
        "libprotobuf-cpp-full" + version,
        "libprotobuf-cpp-lite" + version,
    };

    ASSERT_EQ(expect, libs);
}