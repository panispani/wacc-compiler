#!/bin/bash
cat_wacc_files() {
    FILES=$(ls)
    for f in $FILES
    do
        extension=${f##*.}
        if [ -d $f ]
        then
            cd $f
            cat_wacc_files
            cd ".."
        elif [ $extension = "wacc" ]
        then
            # pass it as an argument to parser
            cat $f
        fi
    done
}

cat_wacc_files

