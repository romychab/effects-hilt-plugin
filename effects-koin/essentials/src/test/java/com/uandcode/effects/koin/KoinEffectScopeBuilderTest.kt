@file:OptIn(KoinInternalApi::class)

package com.uandcode.effects.koin

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.getProxy
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.core.annotation.KoinInternalApi
import org.koin.dsl.ScopeDSL
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class KoinEffectScopeBuilderTest {

    @MockK
    private lateinit var scopeDSL: ScopeDSL

    private lateinit var builder: KoinEffectScopeBuilder

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        builder = KoinEffectScopeBuilder(scopeDSL)
    }

    @Test
    fun `effect() registers effect and scopes it`() {
        val effectScope = mockk<EffectScope>()
        val expectedEffect = mockk<Effect>()
        every { effectScope.getProxy<Effect>() } returns expectedEffect
        val module = module {
            scope<MyScope> {
                scoped { effectScope }
                builder = KoinEffectScopeBuilder(this)
                builder.effect<Effect>()
            }
        }
        val koin = koinApplication {
            modules(module)
        }.koin

        val effect = koin.createScope<MyScope>().get<Effect>()

        assertTrue(builder.registeredEffects.contains(Effect::class))
        assertSame(expectedEffect, effect)
    }

    private interface Effect
    private class MyScope
}