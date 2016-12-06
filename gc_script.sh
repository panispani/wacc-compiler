#!/bin/bash
arm-linux-gnueabi-gcc -std=c++11 gc.cpp -c -o gc.o
arm-linux-gnueabi-gcc -c -o asm.o asm.s -mcpu=arm1176jzf-s -mtune=arm1176jzf-s
arm-linux-gnueabi-gcc gc.o asm.o
qemu-arm -L /usr/arm-linux-gnueabi/ a.out 
