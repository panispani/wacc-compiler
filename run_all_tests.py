#!/usr/bin/python

import os
import commands

correct = 98
total = 98

for root, dirs, files in os.walk("wacc_examples/valid"):
    for file in files:
        if not file.endswith(".wacc") or root == "wacc_examples/valid/advanced":
            continue

        full_path = os.path.join(root, file)

        print(full_path)
        input_file = os.path.splitext(full_path)[0] + ".in"
        if not os.path.isfile(input_file):
            input_file = "empty.in"

        ref_out = commands.getoutput("wacc_examples/refCompile -x {} < {}".format(full_path, input_file))
        our_out = commands.getoutput("./execute.sh {} < {}".format(full_path, input_file))
        ref_out = ref_out.split("\n")
        program_output = ""
        outputting = False

        for line in ref_out:
            if line == "===========================================================":
                outputting = not outputting
                continue

            if outputting:
                program_output += line + "\n"

        program_output = program_output[:-1]

        if program_output != our_out:
            print("FAIL")
            print("Theirs (Length {})".format(len(program_output)))
            print(program_output)
            print("Ours (Length {})".format(len(our_out)))
            print(our_out)
        else:
            correct = correct + 1

        total = total + 1

print("TEST CASES PASSED: {}/{} ({0:.2f}%)".format(correct, total, correct / total * 100.0))




