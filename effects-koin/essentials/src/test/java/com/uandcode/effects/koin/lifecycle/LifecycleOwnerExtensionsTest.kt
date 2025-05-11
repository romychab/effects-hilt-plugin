package com.uandcode.effects.koin.lifecycle

import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.getController
import com.uandcode.effects.core.testing.lifecycle.TestLifecycleOwner
import com.uandcode.effects.koin.lifecycle.mocks.AndroidScopeComponentLifecycleOwner
import com.uandcode.effects.koin.lifecycle.mocks.KoinComponentLifecycleOwner
import com.uandcode.effects.koin.lifecycle.mocks.KoinScopeComponentLifecycleOwner
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.scope.Scope

class LifecycleOwnerExtensionsTest {

    @MockK
    private lateinit var koin: Koin

    @MockK
    private lateinit var effectScope: EffectScope

    @MockK
    private lateinit var koinScope: Scope

    @MockK(relaxUnitFun = true)
    private lateinit var controller: EffectController<EffectImpl>

    private lateinit var effect: EffectImpl

    @MockK
    private lateinit var effectProvider: () -> EffectImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        effect = EffectImpl()
        every { effectProvider.invoke() } returns effect
        every { koin.get<EffectScope>() } returns effectScope
        every { koinScope.get<EffectScope>() } returns effectScope
        every { effectScope.getController<EffectImpl>() } returns controller
        every { controller.isStarted } returns false
    }

    @Test
    fun `verify lazyEffect on common LifecycleOwner`() {
        try {
            mockkObject(GlobalContext)
            every { GlobalContext.get() } returns koin

            val lifecycleOwner = TestLifecycleOwner()
            val effect by lifecycleOwner.lazyEffect(effectProvider)

            verifyLazyEffect(lifecycleOwner) { effect }
        } finally {
            unmockkObject(GlobalContext)
        }
    }

    @Test
    fun `verify lazyEffect on KoinComponent`() {
        val lifecycleOwner = TestLifecycleOwner()
        val koinComponent = KoinComponentLifecycleOwner(
            lifecycleOwner, koin
        )

        val effect by koinComponent.lazyEffect(effectProvider)

        verifyLazyEffect(lifecycleOwner) { effect }
    }

    @Test
    fun `verify lazyEffect on KoinScopeComponent`() {
        val lifecycleOwner = TestLifecycleOwner()
        val koinComponent = KoinScopeComponentLifecycleOwner(
            lifecycleOwner, koin, koinScope,
        )

        val effect by koinComponent.lazyEffect(effectProvider)

        verifyLazyEffect(lifecycleOwner) { effect }
        verify(exactly = 1) {
            koinScope.get<EffectScope>()
        }
        verify(exactly = 0) {
            koin.get<EffectScope>()
        }
    }

    @Test
    fun `verify lazyEffect on AndroidScopeComponent`() {
        val lifecycleOwner = TestLifecycleOwner()
        val koinComponent = AndroidScopeComponentLifecycleOwner(
            lifecycleOwner, koinScope,
        )

        val effect by koinComponent.lazyEffect(effectProvider)

        verifyLazyEffect(lifecycleOwner) { effect }
        verify(exactly = 1) {
            koinScope.get<EffectScope>()
        }
        verify(exactly = 0) {
            koin.get<EffectScope>()
        }
    }

    private fun verifyLazyEffect(
        lifecycleOwner: TestLifecycleOwner,
        effect: () -> EffectImpl,
    ) {
        // verify effect is not created/started yet
        verify(exactly = 0) {
            effectProvider.invoke()
            controller.start(any())
            controller.stop()
        }
        // verify lazy initialization
        assertSame(effect(), this.effect)
        assertSame(effect(), this.effect)
        verify(exactly = 1) {
            effectProvider.invoke()
        }
        // verify starting effect
        lifecycleOwner.start()
        verify(exactly = 1) {
            controller.start(effect())
        }
        // verify stopping effect
        lifecycleOwner.stop()
        verify(exactly = 1) {
            controller.stop()
        }
    }

    private interface Effect
    private class EffectImpl : Effect
}