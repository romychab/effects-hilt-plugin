package com.uandcode.effects.core.compose.testing.internal

import androidx.compose.runtime.AbstractApplier

internal class TestNodeApplier(
    rootNode: Node,
): AbstractApplier<Node>(rootNode) {

    override fun onClear() {
        root.children.clear()
    }

    override fun insertBottomUp(index: Int, instance: Node) {
        current.children.add(index, instance)
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.children.move(from, to, count)
    }

    override fun remove(index: Int, count: Int) {
        current.children.remove(index, count)
    }

    override fun insertTopDown(index: Int, instance: Node) = Unit

}
