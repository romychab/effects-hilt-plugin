package com.uandcode.effects.core.testing.mocks

import com.uandcode.effects.core.annotations.EffectClass

public interface EffectWithDefaultMethod {
    public fun run(input: String)
    public fun defaultRun(input: String) {
        run("default $input")
    }
    public fun combinedDefaultRun(input: String) {
        defaultRun("combined $input")
    }
}

@EffectClass
public class EffectWithDefaultMethodImpl : EffectWithDefaultMethod {
    override fun run(input: String): Unit = Unit
    override fun defaultRun(input: String): Unit = Unit
}
