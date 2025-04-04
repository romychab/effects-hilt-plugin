package com.uandcode.effects.core.mocks

import com.uandcode.effects.core.annotations.EffectClass

public interface EffectWithDefaultTarget {
    public fun run(input: String)
}

@EffectClass
public class EffectWithDefaultTargetImpl : EffectWithDefaultTarget {
    override fun run(input: String): Unit = Unit
}
