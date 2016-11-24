#!/bin/bash
$(pwd)/compile $1 -stdout>"file.s"
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
done <$1

if [ "$actual" == "correct" ]
then
  echo "output correct for "$1"."
  echo "expected: "$expected""
else
  echo "error for "$1" expected: !"$expected"! actual: !"$actual"!"
  size=${#actual}
  echo $size
fi
