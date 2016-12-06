#!/bin/bash
arm-linux-gnueabi-g++ -S -mcpu=arm1176jzf-s -mtune=arm1176jzf-s -std=c++11 gc/gc.cpp -o gc.s
arm-linux-gnueabi-g++ -mcpu=arm1176jzf-s -mtune=arm1176jzf-s -std=c++11 gc.s -c -o gc.o
arm-linux-gnueabi-g++ -c -o asm.o asm.s -mcpu=arm1176jzf-s -mtune=arm1176jzf-s
arm-linux-gnueabi-g++ -mcpu=arm1176jzf-s -mtune=arm1176jzf-s -o output gc.o asm.o
qemu-arm -L /usr/arm-linux-gnueabi/ output
