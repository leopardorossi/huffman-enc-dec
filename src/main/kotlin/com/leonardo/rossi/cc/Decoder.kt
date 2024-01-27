package com.leonardo.rossi.cc

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.lang.StringBuilder
import kotlin.experimental.and

/**
 * This class is responsible for decoding a file using Huffman technique.
 */
class Decoder {

    private class RawContent(val data: StringBuilder, val encTable: Map<String, Char>)

    /**
     * Decodes [input] saving te decoding result to [output].
     * @param input The file to decode.
     * @param output Where to save decoding result.
     */
    fun decode(input: File, output: File) {
        // Read the input file and extract the reverse encoding table and the file's body.
        val rawContent = DataInputStream(input.inputStream()).use { inSt ->
            // Read the header
            val padding = inSt.readInt()
            val encTable = getReverseEncTableFromHeader(inSt)
            // Read file's body. Be aware that the content may be padded
            val body = ByteArray(inSt.available())
            inSt.read(body)
            // Consider each element in the body as a binary string properly formatted to a eight length string.
            val sb = StringBuilder()
            body.forEach {
                // Each element of the body is a byte. When it is transformed to an int JVM uses sign extension. To
                // mitigate this, converted integers are masked with 0xFF. See https://mkyong.com/java/java-sign-extension
                val b = it.toInt() and 0xFF
                val s = String.format("%8s", b.toString(2)).replace(" ", "0")
                sb.append(s)
            }
            // Remove the padding if any
            sb.setLength(sb.length - padding)

            RawContent(sb, encTable)
        }

        // If there is any data, save it to the output file
        if (rawContent.data.isNotEmpty()) {
            DataOutputStream(output.outputStream()).use { wr ->
                wr.bufferedWriter().use {
                    var code = ""
                    for (i in rawContent.data.indices) {
                        code += rawContent.data[i]
                        if (rawContent.encTable.containsKey(code)) {
                            it.write(rawContent.encTable[code]!!.code)
                            code = ""
                        }
                    }
                }

            }
        }
    }

    /**
     * Reads the encoding header from the input stream and extracts the reverse encoding table. It is called reversed,
     * because it maps a Huffman code to its character which is the opposite of what done during encoding phase.
     * @param inSt The input stream where to read the header.
     * @return The reverse encoding table.
     */
    private fun getReverseEncTableFromHeader(inSt: DataInputStream): Map<String, Char> {
        val encTable = mutableMapOf<String, Char>()
        val tableSize = inSt.readInt()
        for (i in 0 until tableSize) {
            // Read the chat and its Huffman code's length. JVM sign extension must be considered.
            val c = inSt.readChar()
            val codeLength = inSt.readInt() and 0xFF
            // Reconstruct Huffman code taking into account that the code may have been padded during encoding
            val code = buildString {
                var bitsToRead = codeLength
                val chunk = inSt.readByte()
                // Extract significant bits from the chunk remembering that, during encoding, padding may be added
                // at the end.
                while (bitsToRead > 0) {
                    var j = 7
                    while (j >= 0 && bitsToRead > 0) {
                        val isOne = (chunk.rotateRight(j) and 1).toInt() == 1
                        if (isOne) append("1") else append("0")
                        bitsToRead--
                        j--
                    }
                }
            }

            encTable[code] = c
        }

        return encTable
    }
}