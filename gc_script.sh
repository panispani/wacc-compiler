#!/bin/bash
COMPILER_PATH=$(dirname $0)/compile

SOURCE_PATH=$1
SOURCE_FILE=$(basename ${SOURCE_PATH})

ASM=${SOURCE_FILE%wacc}s

${COMPILER_PATH} ${SOURCE_PATH} && \
arm-linux-gnueabi-g++ -c -mcpu=arm1176jzf-s -mtune=arm1176jzf-s -std=c++11 gc/gc.cpp -o gc.o && \
arm-linux-gnueabi-g++ -c -mcpu=arm1176jzf-s -mtune=arm1176jzf-s -std=c++11 gc/objects.cpp -o objects.o && \
arm-linux-gnueabi-g++ -c -o asm.o ${ASM} -mcpu=arm1176jzf-s -mtune=arm1176jzf-s && \
arm-linux-gnueabi-g++ -mcpu=arm1176jzf-s -mtune=arm1176jzf-s -o output gc.o asm.o objects.o && \
qemu-arm -L /usr/arm-linux-gnueabi/ output
