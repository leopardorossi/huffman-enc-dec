package com.leonardo.rossi.cc.model

class Leaf(
    private val char: Char,
    private val weight: Int
): TreeNode {
    override fun isLeaf(): Boolean = true

    override fun weight(): Int = weight

    fun char(): Char = char
}