# Huffman compression tool
This repository is an implementation of Huffman compression tool for text files, and it has been inspired by John 
Crickett and its [Coding Challenge newsletter](https://codingchallenges.fyi/). 

Compressing files is a fundamental operation in every operating system to save disk space in exchange for a penalty in
running time. This tool is focused on text files which can be viewed as the concatenation of a set
of symbols expressed in a particular encoding. Each symbol, or character, is expressed as a fixed-length code in 
computer programs. For example, ASCII coding uses eight bits to represent characters.

In terms of space requirements we can say that if we need to represent `n` different characters we need `log(n)` bits. If
each character is used the same amount of times in a text document, fixed-length codes are the most space efficient way
to save information. However, this is not that true in text files we are used to deal with every day: characters
distribution may vary a lot. In such a situation fixed-length code loses its effectiveness, and it is here that
compression techniques come into rescue.

The vary basic idea behind most compression tools is the following: if a character appears more often than another it
should be represented with a shorter code to save space. And this is exactly how Huffman helps us to save disk space.

## Encoder
Huffman encoder does the following:
1. Calculates the frequency of each character in the file.
2. Builds a Huffman tree based on the frequency map. This tree is constructed such that each character is a leaf node;
while internal nodes are weighted summing together characters' frequencies. Each edge in the tree is then labeled with
`0` or `1`. A character's code will be the concatenation of the bits in the path from the root to the character itself.
3. Maps its character in the original file to its code saving its new representation to the destination file. This one
will have a prefix where are stored the frequency map and other compression related information. This header will be
fundamental to decompress the file.

## Decoder
Huffman decoder does the following:
1. Reads the header from the encoded file and generates a reverse encoding table, that is a map where each Huffman code
is assigned to its original character.
2. Reads the encoded content and bit per bit remaps each Huffman code to its original representation.

## How to use
> `huff <input> <operation> <output>`

* `<input>` is the path to the file to encode or decode.
* `<operation>` can be either `e` for _encode_ or `d` for _decode_.
* `<output>` is the path and the name that will have either the encoded or the decoded file.

## Credits
* John Crickett [Coding Challenge newsletter](https://codingchallenges.fyi/)