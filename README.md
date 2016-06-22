# File-Compression-Utility

This is an application that compresses a text file using Huffman coding. Uses min-heap binary tree in C to create a dictionary on the basis of which characters are encoded and decoded. More frequently occurring characters are represented in less bits (instead of the standard ASCII 8-bit scheme), thus saving space. An encoder and a decoder have been programmed in JAVA to use this dictionary to compress and decompress files. The dictionary needs to be provided while decoding.

#Usage

Put the text file you want to compress and rename it as 'source.dat'. Run the script file 'test.sh'.
Dictionary created is 'Dictionary.dic', encoded compressed file is 'out_enc.dat' and extra bit is stored in 'extra.bit'. All three files are necessary for decompression.
After decompressing, only one ouput file called 'out_dec.dat' is made that is basically a copy of 'source.dat'.
