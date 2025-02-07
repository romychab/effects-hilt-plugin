package com.elveum.effects.compose.v2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.elveum.effects.compose.v2.impl.ComposeEffectNodeImpl
import com.elveum.effects.compose.v2.impl.ComposeLifecycleObserver

public val LocalComposeEffectNode: ProvidableCompositionLocal<ComposeEffectNode?> =
    staticCompositionLocalOf { null }

@Composable
public inline fun <reified T: Any> getEffect(): T {
    return LocalComposeEffectNode.current?.getEffect(T::class)
        ?: throw IllegalStateException("getEffect() can be called only within EffectProvider { ... }")
}

@Composable
public fun EffectProvider(
    vararg effects: Any,
    content: @Composable () -> Unit,
) {
    val activity = LocalContext.current.getHostActivity()
    val currentComposeEffectNode = LocalComposeEffectNode.current
    val effectList = effects.toList()
    val newComposeEffectNode = remember(effectList) {
        currentComposeEffectNode?.attachNode(effectList)
            ?: ComposeEffectNodeImpl(effectList, activity)
    }
    CompositionLocalProvider(
        LocalComposeEffectNode provides newComposeEffectNode,
    ) {
        ComposeLifecycleObserver(
            onStart = newComposeEffectNode::start,
            onStop = newComposeEffectNode::stop,
        )
        content()
    }
}

