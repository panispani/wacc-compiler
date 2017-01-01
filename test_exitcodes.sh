#!/bin/bash

# Compile tests and check if the expected exitcodes match

test_wacc_files() {
    EXITCODE="$1"
    FILES=$(ls)
    for f in $FILES
    do
        extension=${f##*.}
        if [ -d $f ]
        then
            cd $f
            test_wacc_files $EXITCODE
            cd ".."
        elif [ $extension = "wacc" ]
        then
            $BASE_DIR/compile $f
            if [ $? -eq $EXITCODE ]
            then
                correct=$((correct+1))
            else
                echo "error for "$f
            fi
            total=$((total+1))
        fi
    done
}

if [ $# -eq 0 ]
then
    make
fi
BASE_DIR=$(pwd)
echo "Running valid tests"
cd "wacc_examples/valid"
correct=0
total=0
test_wacc_files 0
echo "TOTAL FILES: "$total
echo "CORRECT    : "$correct

echo "Running syntactically invalid tests"
cd "../invalid/syntaxErr"
correct=0
total=0
test_wacc_files 100
echo "TOTAL FILES: "$total
echo "CORRECT    : "$correct

echo "Running semantically invalid tests"
cd "../semanticErr"
correct=0
total=0
test_wacc_files 200
echo "TOTAL FILES: "$total
echo "CORRECT    : "$correct
