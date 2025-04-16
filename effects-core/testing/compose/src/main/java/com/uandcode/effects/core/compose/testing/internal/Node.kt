package com.uandcode.effects.core.compose.testing.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode

internal open class Node(
    var name: String = "",
) {
    internal val children: MutableList<Node> = mutableListOf()

    fun find(name: String): Node? {
        if (this.name == name) return this
        return children.firstNotNullOfOrNull {
            it.find(name)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Node) return false
        if (this::class != other::class) return false
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

}

@Composable
public fun TestElement(
    name: String
) {
    ComposeNode<Node, TestNodeApplier>(
        factory = ::Node,
        update = {
            set(name) {
                this.name = it
            }
        },
    )
}
