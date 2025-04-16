package com.uandcode.effects.core.testing.mocks

import com.uandcode.effects.core.annotations.EffectClass

public interface EffectWithOverriddenClose : AutoCloseable {
    public fun run(input: String)
    public override fun close(): Unit = Unit
}

@EffectClass
public class EffectWithOverriddenCloseImpl : EffectWithOverriddenClose {
    override fun run(input: String): Unit = Unit
}
