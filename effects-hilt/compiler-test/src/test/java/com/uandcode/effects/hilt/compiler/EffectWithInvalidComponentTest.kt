package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class EffectWithInvalidComponentTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.hilt.annotations.HiltEffect
        import dagger.hilt.android.components.ServiceComponent
        
        interface Effect

        @HiltEffect(
            installIn = ServiceComponent::class
        )
        class EffectImpl : Effect

    """.trimIndent()

    @Test
    fun `compilation of effect installed to ServiceComponent should fail`() = with(compile(source)){
        assertCompileError("Input.kt:8: Invalid Hilt Component is specified in @HiltEffect(installIn = ...). Example of valid Hilt components: SingletonComponent::class, ActivityRetainedComponent::class, ViewModelComponent::class, ActivityComponent::class, FragmentComponent::class")
    }
}