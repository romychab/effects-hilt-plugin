package com.uandcode.effects.core.testing.mocks

import com.uandcode.effects.core.annotations.EffectClass

public interface EffectWithNonOverriddenClose : AutoCloseable {
    public fun run(input: String)
}

@EffectClass
public class EffectWithNonOverriddenCloseImpl : EffectWithNonOverriddenClose {
    override fun run(input: String): Unit = Unit
    override fun close(): Unit = Unit
}
