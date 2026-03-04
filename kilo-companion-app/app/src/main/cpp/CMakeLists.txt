# =============================================================================
# CMakeLists.txt - Native build configuration for Node.js integration
# =============================================================================

cmake_minimum_required(VERSION 3.10.0)
project("codepal-node")

# Find Android NDK
find_package(androidndk REQUIRED)

# Include directories
include_directories(
    ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/include
    ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/include/node
)

# Link directories
link_directories(
    ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}
)

# Build the native library
add_library(
    codepal-node
    SHARED
    native-node.cpp
)

# Link libraries
target_link_libraries(
    codepal-node
    nodejs-mobile
    android
    log
    atomic
)

# Compiler flags
target_compile_options(codepal-node PRIVATE
    -fPIC
    -O2
    -Wall
)

# Set output name
set_target_properties(codepal-node PROPERTIES
    LIBRARY_OUTPUT_NAME "codepal-node"
)
