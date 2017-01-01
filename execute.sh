#!/bin/bash

# Compiles a WACC file to ARM11 assembly
# Creates an executable with a cross-compiler for ARM11
# Emulates the created executable

COMPILER_PATH=$(dirname $0)/compile

SOURCE_PATH=$1
SOURCE_FILE=$(basename ${SOURCE_PATH})

ASM=${SOURCE_FILE%wacc}s

${COMPILER_PATH} ${SOURCE_PATH} && \
arm-linux-gnueabi-g++ -c -o asm.o ${ASM} -mcpu=arm1176jzf-s -mtune=arm1176jzf-s && \
arm-linux-gnueabi-g++ -mcpu=arm1176jzf-s -mtune=arm1176jzf-s -o output gc.o asm.o objects.o && \
qemu-arm -L /usr/arm-linux-gnueabi/ output
