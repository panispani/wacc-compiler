cmake_minimum_required(VERSION 3.5)
project(gc)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    gc.cpp
    gc.hpp
    objects.cpp
    objects.hpp
    tests.cpp)

add_executable(gc ${SOURCE_FILES})
set_target_properties(gc PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")