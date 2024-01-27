package com.leonardo.rossi.cc.model

class InternalNode(
    private val weight: Int,
    private val left: TreeNode,
    private val right: TreeNode
): TreeNode {

    companion object {
        const val LEFT_CODE = "0"
        const val RIGHT_CODE = "1"
    }

    override fun isLeaf(): Boolean = false

    override fun weight(): Int = weight

    fun left(): TreeNode = left

    fun right(): TreeNode = right
}