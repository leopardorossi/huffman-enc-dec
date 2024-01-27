package com.leonardo.rossi.cc

import com.leonardo.rossi.cc.model.HuffmanTree
import java.io.DataOutputStream
import java.io.File
import java.io.FileReader
import java.lang.StringBuilder
import kotlin.math.ceil
import kotlin.math.min

/**
 * This class is responsible for encoding a file using Huffman technique.
 *
 * The encoding operation shrinks input's file size, helping to save disk space.
 */
class Encoder {

    private class DataPrepResult(val data: ByteArray, val padding: Int)

    /**
     * Encodes [input] using Huffman technique. Encoding result will be saved in [output].
     *
     * Encoding a file with Huffman consists in the following steps:
     * 1. Create a frequency map where each character in [input] is paired with how many times it appears in it.
     * 2. Build an Huffman tree based on the frequency map. Tree's leaves will be the characters with the lowest
     * frequency.
     * 3. Create an encoding table, where each character is linked to a binary string which corresponds to the path
     * from the root to the character.
     * 4. Encode [input] mapping each character to its binary string.
     * @param input The file to encode.
     * @param output Where to save the encoding result.
     */
    fun encode(input: File, output: File) {
        // Build a Huffman tree based on the frequencies of input's characters
        val frequencies = calcCharFrequencies(input)
        val tree = HuffmanTree.createTree(frequencies)

        // Get the encoding table from the tree and encode input's content
        val encTable = tree.encodingTable()
        val encContentBuilder = StringBuilder()
        input.bufferedReader().use { br ->
            var c = br.read()
            while (c > 0) {
                encContentBuilder.append(encTable[c.toChar()])
                c = br.read()
            }
        }

        writeEncodedFile(output, encTable, prepareData(encContentBuilder))
    }

    /**
     * Calculates how many times each character appears in [input]
     * @param input The file to inspect.
     * @return A map where each character in [input] is paired with its frequency in it.
     */
    private fun calcCharFrequencies(input: File): Map<Char, Int> =
        FileReader(input).use { rd ->
            val frequencies = mutableMapOf<Char, Int>()
            var c = rd.read()
            while (c > 0) {
                val freq = frequencies.getOrDefault(c.toChar(), 0)
                frequencies[c.toChar()] = freq + 1
                c = rd.read()
            }
            frequencies
        }

    /**
     * Prepares data into an array of bytes taking into account of any additional padding.
     *
     * This padding is added iff data length is not a multiple of byte.
     * @param dataBuilder The data to prepare.
     * @return An object with a byte array and padding if necessary.
     */
    private fun prepareData(dataBuilder: StringBuilder): DataPrepResult {
        // Calculate how many bytes are needed to represent data and initialize the output array accordingly
        val neededBytes = ceil(dataBuilder.length.toDouble() / 8.0).toInt()
        val output = ByteArray(neededBytes)

        // Loop over the encoded text at group of 8 characters (bytes) and store it in the output array. It must be
        // considered that, if data length is not a multiple of byte, some padding must be added to the last group.
        for (i in dataBuilder.indices step 8) {
            var stringByte = dataBuilder.substring(i, min(i + 8, dataBuilder.length))
            if (stringByte.length < 8)
                stringByte += "0".repeat(8 - stringByte.length)
            output[i / 8] = stringByte.toInt(2).toByte()
        }

        // Calculate the padding
        var padding = 8 - dataBuilder.length % 8
        if (padding == 8) padding = 0

        return DataPrepResult(output, padding)
    }

    /**
     * Writes the encoded file adding to it a header that contains any additional padding and the encoding table.
     *
     * The header will be fundamental in decoding phase.
     * @param output Where to write encoded file.
     * @param encTable The encoding table. This will be written as an header in the file.
     * @param preparedData The data to write in the file.
     */
    private fun writeEncodedFile(output: File, encTable: Map<Char, String>, preparedData: DataPrepResult) {
        DataOutputStream(output.outputStream()).use { out ->
            // Write a header with char encodings
            out.writeInt(preparedData.padding)
            out.writeInt(encTable.size)
            encTable.entries.forEach { entry ->
                out.writeChar(entry.key.code)
                out.writeInt(entry.value.length)
                writeBinaryStringAsChars(entry.value, out)
            }
            // Write content
            out.write(preparedData.data)
        }
    }

    /**
     * Writes [binaryString] to [out]. The string will be divided in chunks of 8 bits, adding padding when necessary.
     * Each 8-bits group will be then directly written as a byte to the destination file.
     * @param binaryString The string to write.
     * @param out Where to write the binary string.
     */
    private fun writeBinaryStringAsChars(binaryString: String, out: DataOutputStream) {
        binaryString
            .asSequence()
            .chunked(8)
            .takeWhile { it.isNotEmpty() }
            .map { bits ->
                val padding = MutableList(8 - bits.size) { '0' }
                bits + padding
            }
            .map { bits -> bits.joinToString(separator = "").toInt(2) }
            .forEach { out.writeByte(it) }
    }
}