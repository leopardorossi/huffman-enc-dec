package com.leonardo.rossi.cc.model

interface TreeNode {
    /**
     * Determines if a node is a leaf or not.
     * @return True if the node is a leaf, false otheriwse.
     */
    fun isLeaf(): Boolean

    /**
     * Returns the weight associated to a node.
     * @return The node's weight.
     */
    fun weight(): Int
}
