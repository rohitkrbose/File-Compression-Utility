#!/bin/bash

clear

echo "Compiling"
gcc MakeDictionary.c -o MakeD.out
javac Encode.java
javac Decode.java

echo "Encoding"
./MakeD.out
java Encode

echo "Decoding"
java Decode

echo "Deleting backup files"
rm MakeD.out
rm Encode.class
rm Decode.class

echo "DONE"

clear
