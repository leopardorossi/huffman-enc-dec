package com.leonardo.rossi.cc

import com.leonardo.rossi.cc.model.HuffmanTree
import com.leonardo.rossi.cc.model.InternalNode
import com.leonardo.rossi.cc.model.Leaf
import com.leonardo.rossi.cc.model.TreeNode
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.io.File
import java.util.PriorityQueue
import java.util.concurrent.Callable

@Command(
    name = "huff",
    description = ["An implementation of the Huffman encoder/decoder"]
)
class HuffmanEncDec: Callable<Int> {

    @Parameters(index = "0", description = ["The file to encode or to decode"])
    lateinit var input: File

    @Parameters(
        index = "1",
        description = ["Encode or decode the file. Use 'e' to compress, 'd' to decode"],
        defaultValue = "c"
    )
    var operation: Char? = null

    @Parameters(index = "2")
    lateinit var output: File

    override fun call(): Int {

        when (operation) {
            'e' -> {
                val enc = Encoder()
                enc.encode(input, output)
            }
            'd' -> {
                val dec = Decoder()
                dec.decode(input, output)
            }
            else -> return 1
        }

        return 0
    }
}