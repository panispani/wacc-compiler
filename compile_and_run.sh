#!/bin/bash

# Compiles a WACC file to ARM11 assembly
# Creates an executable with a cross-compiler for ARM11
# Emulates the created executable

SOURCE_PATH=$1
SOURCE_FILE=$(basename ${SOURCE_PATH})

ASM=${SOURCE_FILE%wacc}s
EXE=${SOURCE_FILE%.wacc}

# TODO: remove assembly file in case of compilation errors
$(pwd)/compile ${SOURCE_PATH} > ${ASM} && \
arm-linux-gnueabi-gcc -o ${EXE} -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ${ASM} && \
qemu-arm -L /usr/arm-linux-gnueabi/ ${EXE}