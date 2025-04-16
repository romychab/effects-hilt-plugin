package com.uandcode.effects.core.testing.mocks

import com.uandcode.effects.core.annotations.EffectClass

public interface TargetEffect1 {
    public fun run1(input: String)
}

public interface TargetEffect2 {
    public fun run2(input: String)
}

public interface NonTargetEffect3 {
    public fun run3(input: String)
}

@EffectClass(
    targets = [TargetEffect1::class, TargetEffect2::class],
)
public class CombinedEffectWithTarget : TargetEffect1, TargetEffect2, NonTargetEffect3 {
    override fun run1(input: String): Unit = Unit
    override fun run2(input: String): Unit = Unit
    override fun run3(input: String): Unit = Unit
}