cmake_minimum_required(VERSION 3.5)
project(gc)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    gc.cpp
    gc.hpp
    objects.cpp
    objects.hpp)

add_executable(gc ${SOURCE_FILES})