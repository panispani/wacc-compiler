# Sample Makefile for the WACC Compiler lab: edit this to build your own comiler
# Locations

ANTLR_DIR	  := antlr
SOURCE_DIR	:= src
OUTPUT_DIR	:= bin 
SBT					?= sbt

# Tools

ANTLR	 := antlrBuild
FIND	 := find
RM	   := rm -rf
MKDIR	 := mkdir -p
JAVA	 := java
JAVAC	 := javac
ARMG++ := arm-linux-gnueabi-g++

JFLAGS	:= -sourcepath $(SOURCE_DIR) -d $(OUTPUT_DIR) -cp lib/antlr-4.5.3-complete.jar
ARMFLAGS := -c -mcpu=arm1176jzf-s -mtune=arm1176jzf-s -std=c++11

# the make rules

all: rules

# runs the antlr build script then attempts to compile all .java files within src
rules:
	cd $(ANTLR_DIR) && ./$(ANTLR) 
	$(FIND) $(SOURCE_DIR) -name '*.java' > $@
	$(MKDIR) $(OUTPUT_DIR)
	$(JAVAC) $(JFLAGS) @$@
	$(ARMG++) $(ARMFLAGS) gc/gc.cpp -o gc.o
	$(ARMG++) $(ARMFLAGS) gc/objects.cpp -o objects.o
	$(RM) rules
	$(SBT) package

clean:
	$(RM) rules $(OUTPUT_DIR)
	$(RM) objects.o gc.o

.PHONY: all rules clean
