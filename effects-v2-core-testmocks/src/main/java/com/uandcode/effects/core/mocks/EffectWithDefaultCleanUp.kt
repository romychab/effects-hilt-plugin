package com.uandcode.effects.core.mocks

import com.uandcode.effects.core.annotations.EffectClass

public interface EffectWithDefaultCleanUp {
    public fun run(input: String)
    public fun cleanUp(): Unit = Unit
}

@EffectClass
public class EffectWithDefaultCleanUpImpl : EffectWithDefaultCleanUp {
    override fun run(input: String): Unit = Unit
}
