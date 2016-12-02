#!/usr/bin/python

import os
import commands
import sys

correct = 0
total = 0

for root, dirs, files in os.walk("wacc_examples"):
    for file in files:
        if not file.endswith(".wacc") or root == "wacc_examples/valid/advanced":
            continue

        if file == "echoBigNegInt.wacc" or file == "echoNegInt.wacc" or file == "hiddenDoubleFree.wacc" or file == "doubleFree.wacc":
            continue

        full_path = os.path.join(root, file)

        print(full_path)
        input_file = os.path.splitext(full_path)[0] + ".in"
        if not os.path.isfile(input_file):
            input_file = "empty.in"

        (ref_status, ref_out) = commands.getstatusoutput("wacc_examples/refCompile -x {} < {}".format(full_path, input_file))
        (our_status, our_out) = commands.getstatusoutput("./execute.sh {} < {}".format(full_path, input_file))
        our_status = os.WEXITSTATUS(our_status)
        ref_status = -1

        if (os.path.isfile(os.path.splitext(file)[0] + ".s")):
            os.remove(os.path.splitext(file)[0] + ".s")

        if (os.path.isfile(os.path.splitext(file)[0])):
            os.remove(os.path.splitext(file)[0])

        ref_out = ref_out.split("\n")
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
                program_output += line + "\n"

        program_output = program_output[:-1]

        if root.startswith("wacc_examples/invalid") and ref_status != our_status:
            print("==================STATUS MISMATCH==========================")
            print("Theirs: {}".format(ref_status))
            print("Ours  : {}".format(our_status))
            print("==================/STATUS MISMATCH==========================")
        elif root.startswith("wacc_examples/valid") and program_output != our_out:
            print("=================OUTPUT MISMATCH===========================")
            print("Theirs (Length {}):".format(len(program_output)))
            print(program_output)
            print("Ours (Length {}):".format(len(our_out)))
            print(our_out)
            print("================/OUTPUT MISMATCH===========================")
        else:
             correct = correct + 1

        total = total + 1

print("TEST CASES PASSED: {}/{} ({:.2f}%)".format(correct, total, correct / total * 100.0))

if correct != total:
    sys.exit(1)


