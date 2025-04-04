package com.uandcode.effects.core.mocks

import com.uandcode.effects.core.annotations.EffectClass

public interface EffectWithCleanUp {
    public fun run(input: String)
    public fun destroy(): Unit = Unit
}

@EffectClass(
    cleanUpMethodName = "destroy",
)
public class EffectWithCleanUpImpl : EffectWithCleanUp {
    override fun run(input: String): Unit = Unit
}
