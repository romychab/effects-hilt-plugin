package com.elveum.effects.compose.v2

import kotlin.reflect.KClass

public interface ComposeEffectNode {
    public fun <T : Any> getEffect(clazz: KClass<T>): T
    public fun attachNode(effects: List<Any>): ComposeEffectNode
    public fun start()
    public fun stop()
}
