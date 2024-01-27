package com.leonardo.rossi.cc.model

import java.util.*

class HuffmanTree private constructor(private val root: TreeNode) {

    companion object {

        /**
         * Creates a Huffman Tree based on the frequencies provided in input.
         * @param frequencies A map that contains characters frequencies.
         * @return An instance of a Huffman Tree for the given frequencies.
         */
        fun createTree(frequencies: Map<Char, Int>): HuffmanTree {
            // Map each entry in the map to a leaf node. Indeed, characters with their frequencies will be the leaves of
            // the Huffman tree. Then, organize them into a priority queue such that the queue's head is the leaf with
            // the lowest frequency.
            val queue = PriorityQueue<TreeNode> { n, m -> n.weight() - m.weight() }
            frequencies.entries
                .asSequence()
                .map { e -> Leaf(e.key, e.value) }
                .forEach { l -> queue.offer(l) }
            // Build the Huffman tree from the queue
            while (queue.size > 1) {
                val first = queue.poll()
                val second = queue.poll()
                val totalWeight = first.weight() + second.weight()
                queue.offer(InternalNode(totalWeight, first, second))
            }
            return HuffmanTree(queue.poll())
        }
    }

    /**
     * Calculates the encoding table that corresponds to the tree.
     */
    fun encodingTable(): Map<Char, String> {
        val encodingTable = mutableMapOf<Char, String>()
        traverseTree(root, "", encodingTable)
        return encodingTable
    }

    /**
     * Traverse the tree and calculates the weight of each node.
     * @param node The current node.
     * @param encodingTable A map where to store each character's Huffman code.
     */
    private fun traverseTree(node: TreeNode, code: String, encodingTable: MutableMap<Char, String>) {
        if (node.isLeaf()) {
            val leaf = node as Leaf
            encodingTable[leaf.char()] = code
            return
        }
        val internal = node as InternalNode
        traverseTree(internal.left(), code + InternalNode.LEFT_CODE, encodingTable)
        traverseTree(internal.right(), code + InternalNode.RIGHT_CODE, encodingTable)
    }
}