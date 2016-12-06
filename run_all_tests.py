#!/usr/bin/python

import os
import commands
import sys
import re

address_pattern = r"0x[0-9a-f]{5}"

def parse_output(ref_out):
    ref_out = ref_out.split("\n")
    ref_status = -9999
    program_output = ""
    outputting = False

    exit_start = "Errors detected during compilation! Exit code "
    for line in ref_out:
        if line.startswith(exit_start):
            ref_status = int(line.split(exit_start)[1].split(" ")[0])

        if line == "===========================================================":
            outputting = not outputting
            continue

        if outputting:
            line = re.sub(address_pattern, "#address#", line)
            program_output += line + "\n"

    program_output = program_output[:-1]
    return ref_status, program_output

def get_input_file(full_path):
    input_file = os.path.splitext(full_path)[0] + ".in"
    if not os.path.isfile(input_file):
        input_file = "empty.in"
    return input_file

def test_file(full_path, invalid, ref_status, ref_out):
    print(full_path)
    input_file = get_input_file(full_path)

    (our_status, our_out) = commands.getstatusoutput("./execute.sh {} < {}".format(full_path, input_file))
    our_status = os.WEXITSTATUS(our_status)

    if (os.path.isfile(os.path.splitext(file)[0] + ".s")):
        os.remove(os.path.splitext(file)[0] + ".s")

    if (os.path.isfile(os.path.splitext(file)[0])):
        os.remove(os.path.splitext(file)[0])

    our_out = re.sub(address_pattern, "#address#", our_out)

    if invalid and ref_status != our_status:
        print("==================STATUS MISMATCH==========================")
        print("Theirs: {}".format(ref_status))
        print("Ours  : {}".format(our_status))
        print("==================/STATUS MISMATCH==========================")
        return False
    
    if not invalid and ref_out != our_out:
        print("=================OUTPUT MISMATCH===========================")
        print("Theirs (Length {}):".format(len(ref_out)))
        print(ref_out)
        print("Ours (Length {}):".format(len(our_out)))
        print(our_out)
        print("================/OUTPUT MISMATCH===========================")
        return False

    return True

def print_stats(name, correct, total):
    print("{} TEST CASES PASSED: {}/{} ({:.1%})".format(name, correct, total, correct / float(total)))

extensions_correct = 0
extensions_total = 0
labts_correct = 0
labts_total = 0

for root, dirs, files in os.walk("extension_examples"):
    for file in files:
        if not file.endswith(".wacc"):
            continue

        full_path = os.path.join(root, file)
        extensions_total += 1
        output_file = os.path.splitext(full_path)[0] + ".out"
        if not os.path.isfile(output_file):
            print("{} HAS NO OUTPUT FILE".format(full_path))
            continue

        output_file = open(output_file, "r").read()[:-1]
        invalid = root.startswith("extension_examples/invalid") 
        if test_file(full_path, invalid, int(output_file) if invalid else 0, output_file):
            extensions_correct += 1

print_stats("EXTENSION", extensions_correct, extensions_total)

for root, dirs, files in os.walk("wacc_examples"):
    for file in files:
        if not file.endswith(".wacc") or root == "wacc_examples/valid/advanced":
            continue

        if file == "echoBigNegInt.wacc" or file == "echoNegInt.wacc" or file == "hiddenDoubleFree.wacc" or file == "doubleFree.wacc":
            continue

        full_path = os.path.join(root, file)
        (ref_status, ref_out) = commands.getstatusoutput("wacc_examples/refCompile -x {} < {}".format(full_path, get_input_file(full_path)))
        (ref_status, ref_out) = parse_output(ref_out)

        if test_file(full_path, full_path.startswith("wacc_examples/invalid"), ref_status, ref_out):
            labts_correct += 1
        labts_total += 1

print_stats("EXTENSION", extensions_correct, extensions_total)
print_stats("LABTS", labts_correct, labts_total)

if labts_correct != labts_total:
    sys.exit(1)

if extensions_correct != extensions_total:
    sys.exit(1)


