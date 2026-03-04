# =============================================================================
# CMakeLists.txt - Native build configuration for Node.js integration
# =============================================================================
# This CMake file builds the JNI bridge between Android and Node.js.
# It links against nodejs-mobile libraries.
# =============================================================================

cmake_minimum_required(VERSION 3.10.0)
project("codepal-node")

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find JNI (provided by Android NDK)
find_package(JNI REQUIRED)

# Check if nodejs-mobile is available
set(NODEJS_MOBILE_DIR ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})
set(NODEJS_MOBILE_LIB ${NODEJS_MOBILE_DIR}/libnodejs-mobile.so)

if(EXISTS ${NODEJS_MOBILE_LIB})
    message(STATUS "nodejs-mobile found for ${ANDROID_ABI}")
    set(NODEJS_MOBILE_AVAILABLE TRUE)
else()
    message(WARNING "nodejs-mobile not found for ${ANDROID_ABI}")
    message(WARNING "Expected: ${NODEJS_MOBILE_LIB}")
    set(NODEJS_MOBILE_AVAILABLE FALSE)
endif()

# Include directories
include_directories(
    ${JNI_INCLUDE_DIRS}
    ${CMAKE_SOURCE_DIR}/../jniLibs/include
    ${CMAKE_SOURCE_DIR}/../jniLibs/include/node
)

if(NODEJS_MOBILE_AVAILABLE)
    include_directories(
        ${NODEJS_MOBILE_DIR}/include
        ${NODEJS_MOBILE_DIR}/include/node
    )
    
    # Link directories
    link_directories(${NODEJS_MOBILE_DIR})
endif()

# Build the native library
add_library(
    codepal-node
    SHARED
    native-node.cpp
)

# Link libraries
target_link_libraries(
    codepal-node
    ${JNI_LIBRARIES}
    android
    log
    atomic
)

if(NODEJS_MOBILE_AVAILABLE)
    target_link_libraries(codepal-node nodejs-mobile)
    target_compile_definitions(codepal-node PRIVATE NODEJS_MOBILE_AVAILABLE=1)
else()
    target_compile_definitions(codepal-node PRIVATE NODEJS_MOBILE_AVAILABLE=0)
endif()

# Compiler flags
target_compile_options(codepal-node PRIVATE
    -fPIC
    -O2
    -Wall
    -Wextra
)

# Set output name
set_target_properties(codepal-node PROPERTIES
    LIBRARY_OUTPUT_NAME "codepal-node"
)
