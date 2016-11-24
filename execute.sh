#!/bin/bash

# Compiles a WACC file to ARM11 assembly
# Creates an executable with a cross-compiler for ARM11
# Emulates the created executable

COMPILER_PATH=$(dirname $0)/compile
EMULATOR_PATH=$(dirname $0)/execute

SOURCE_PATH=$1
SOURCE_FILE=$(basename ${SOURCE_PATH})

ASM=${SOURCE_FILE%wacc}s

${COMPILER_PATH} ${SOURCE_PATH} && ${EMULATOR_PATH} ${ASM}