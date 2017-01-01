#!/bin/bash

# Creates an executable with a cross-compiler for ARM11
# Emulates the created executable

ASM=$1

arm-linux-gnueabi-g++ -c -o asm.o ${ASM} -mcpu=arm1176jzf-s -mtune=arm1176jzf-s
arm-linux-gnueabi-g++ -mcpu=arm1176jzf-s -mtune=arm1176jzf-s -o output gc.o asm.o objects.o
qemu-arm -L /usr/arm-linux-gnueabi/ output
