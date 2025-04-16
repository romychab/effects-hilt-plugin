package com.uandcode.effects.core.compose.testing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import com.uandcode.effects.core.compose.testing.internal.Node
import com.uandcode.effects.core.compose.testing.internal.TestNodeApplier

@Composable
public fun TestElement(name: String) {
    ComposeNode<ElementNode, TestNodeApplier>(
        factory = {
            ElementNode()
        },
        update = {
            set(name) {
                this.name = it
            }
        },
    )
}

internal class ElementNode : Node("")