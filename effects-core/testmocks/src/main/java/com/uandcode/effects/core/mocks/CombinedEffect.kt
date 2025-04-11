package com.uandcode.effects.core.mocks

import com.uandcode.effects.core.annotations.EffectClass

public interface Effect1 {
    public fun run1(input: String)
}

public interface Effect2 {
    public fun run2(input: String)
}

@EffectClass
public class CombinedEffect : Effect1, Effect2 {
    override fun run1(input: String): Unit = Unit
    override fun run2(input: String): Unit = Unit
}