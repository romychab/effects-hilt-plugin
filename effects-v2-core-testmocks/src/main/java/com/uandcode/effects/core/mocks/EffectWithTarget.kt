package com.uandcode.effects.core.mocks

import com.uandcode.effects.core.annotations.EffectClass

public interface EffectWithTarget {
    public fun run(input: String)
}

@EffectClass(
    targets = [EffectWithTarget::class],
)
public class EffectWithTargetImpl : Runnable, EffectWithTarget {
    override fun run(): Unit = Unit
    override fun run(input: String): Unit = Unit
}
