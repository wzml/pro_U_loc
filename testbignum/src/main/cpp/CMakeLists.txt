cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")  #  c++编译
include_directories(D:/android-studio/pro_U_loc/testbignum/src/main/include)

#  动态方式加载库
add_library(openssl
        SHARED
        IMPORTED)
add_library(ssl
        SHARED
        IMPORTED)
#  引入第三方.so库
set_target_properties(openssl
        PROPERTIES
        IMPORTED_LOCATION
        D:/android-studio/pro_U_loc/testbignum/libs/${ANDROID_ABI}/libcrypto.so)
set_target_properties(ssl
        PROPERTIES
        IMPORTED_LOCATION
        D:/android-studio/pro_U_loc/testbignum/libs/${ANDROID_ABI}/libssl.so)

add_library(
        native_lib

        SHARED

        native_lib.cpp)

find_library(
        log-lib

        log)

target_link_libraries(
        native_lib
        openssl
        ssl
        android

        ${log-lib})