package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class MultipleEffectsWithDifferentComponentsTest : AbstractHiltKspTest() {


    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.hilt.annotations.HiltEffect
        import dagger.hilt.android.components.ActivityComponent
        import dagger.hilt.components.SingletonComponent
        
        interface Effect {
            fun oneOffEvent(input: String)
        }

        @HiltEffect(
            installIn = SingletonComponent::class,
        )
        class EffectImpl1 : Effect {
            override fun oneOffEvent(input: String) = Unit
        }

        @HiltEffect(
            installIn = ActivityComponent::class,
        )
        class EffectImpl2 : Effect {
            override fun oneOffEvent(input: String) = Unit
        }

    """.trimIndent()

    @Test
    fun `compilation of 2 effects installed in different components should fail`() = with(compile(source)) {
        assertCompileError("Input.kt:7: Interface 'Effect' has implementations installed in different components: 1) EffectImpl1 is installed in SingletonComponent; 2) EffectImpl2 is installed in ActivityComponent.")
    }

}