#!/bin/bash
run_wacc_files() {
    FILES=$(ls)
    for f in $FILES
    do
        extension=${f##*.}
        if [ -d $f ]
        then
            cd $f
            run_wacc_files
            cd ".."
        elif [ $extension = "wacc" ]
        then
            $BASE_DIR/compile $f>"file.s"
            arm-linux-gnueabi-gcc -o FILENAME1 -mcpu=arm1176jzf-s -mtune=arm1176jzf-s "file.s"
            actual=$(qemu-arm -L /usr/arm-linux-gnueabi/ FILENAME1)

            #find_output
            while read line; do
                  if [ "$line" == "# Output:" ]
                  then
                    read line
                    if [ "$line" == "# #empty#" ]
                    then
                        expected=""
                    else
                        prefix="# "
                        expected=${line#$prefix}
                    fi
                    break
                  fi
            done <$f

            if [ $actual == $expected ]
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
echo "Running valid programs"
cd "wacc_examples/valid"
correct=0
total=0
run_wacc_files
echo "TOTAL FILES: "$total
echo "CORRECT    : "$correct
