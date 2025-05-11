package com.uandcode.effects.koin

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.getProxy
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertSame
import org.junit.Test
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class KoinEffectDeclarationsTest {

    @Test
    fun `Module effect() registers factory`() {
        val effectScope = mockk<EffectScope>()
        val expectedEffect1 = mockk<Effect>()
        val expectedEffect2 = mockk<Effect>()
        every { effectScope.getProxy<Effect>() } returns expectedEffect1 andThen expectedEffect2
        val module = module {
            factory<EffectScope> { effectScope }
            effect<Effect>()
        }
        val koin = koinApplication {
            modules(module)
        }.koin

        val effect1 = koin.get<Effect>()
        val effect2 = koin.get<Effect>()

        assertSame(expectedEffect1, effect1)
        assertSame(expectedEffect2, effect2)
    }

    @Test
    fun `ScopeDSL effect() registers factory`() {
        val effectScope = mockk<EffectScope>()
        val expectedEffect1 = mockk<Effect>()
        val expectedEffect2 = mockk<Effect>()
        every { effectScope.getProxy<Effect>() } returns expectedEffect1 andThen expectedEffect2
        val module = module {
            factory<EffectScope> { effectScope }
            scope<MyScope> {
                effect<Effect>()
            }
        }
        val koin = koinApplication {
            modules(module)
        }.koin
        val scope = koin.createScope<MyScope>()

        val effect1 = scope.get<Effect>()
        val effect2 = scope.get<Effect>()

        assertSame(expectedEffect1, effect1)
        assertSame(expectedEffect2, effect2)
    }

    private interface Effect
    private class MyScope
}