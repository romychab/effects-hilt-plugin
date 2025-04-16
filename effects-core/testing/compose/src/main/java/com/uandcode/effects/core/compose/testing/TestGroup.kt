package com.uandcode.effects.core.compose.testing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import com.uandcode.effects.core.compose.testing.internal.Node
import com.uandcode.effects.core.compose.testing.internal.TestNodeApplier

@Composable
public fun TestGroup(name: String, content: @Composable () -> Unit) {
    ComposeNode<GroupNode, TestNodeApplier>(
        factory = { GroupNode() },
        update = {
            set(name) {
                this.name = it
            }
        },
        content = content,
    )
}

internal class GroupNode : Node("")