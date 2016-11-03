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
            # add options
            $BASE_DIR/grun <$f
        fi
    done
}

make
BASE_DIR=$(pwd)
echo "Running valid tests"
cd "wacc_examples/valid"
cat_wacc_files

echo "Running syntactically invalid tests"
cd "../invalid/syntaxErr"
cat_wacc_files

echo "Running semantically invalid tests"
cd "../invalid/semanticErr"
cat_wacc_files

