package com.uandcode.effects.core.testing.mocks

import com.uandcode.effects.core.annotations.EffectClass

public interface SuperEffect {
    public fun superRun(superInput: String)
    public fun defaultRun(defaultInput: String) {
        superRun("combined $defaultInput")
    }
}

public interface SubEffect : SuperEffect {
    public fun run(input: String)
}

@EffectClass
public class EffectWithHierarchy : SubEffect {
    override fun superRun(superInput: String): Unit = Unit
    override fun run(input: String): Unit = Unit
}

@EffectClass
public class EffectWithExplicitHierarchy : SubEffect, SuperEffect {
    override fun superRun(superInput: String): Unit = Unit
    override fun run(input: String): Unit = Unit
}
