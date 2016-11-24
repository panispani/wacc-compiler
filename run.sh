#!/bin/bash

ASM=$1

EXE=$(basename ${ASM})
EXE=${EXE%.s}

arm-linux-gnueabi-gcc -o ${EXE} -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ${ASM} && \
qemu-arm -L /usr/arm-linux-gnueabi/ ${EXE}