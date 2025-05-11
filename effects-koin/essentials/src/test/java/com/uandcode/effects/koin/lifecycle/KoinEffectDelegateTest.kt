package com.uandcode.effects.koin.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.BoundEffectController
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class KoinEffectDelegateTest {

    @MockK(relaxUnitFun = true)
    private lateinit var controller: BoundEffectController<Effect>

    @MockK
    private lateinit var expectedEffect: Effect

    @MockK
    private lateinit var lifecycleOwner: LifecycleOwner

    @MockK
    private lateinit var controllerProvider: () -> BoundEffectController<Effect>

    private lateinit var delegate: KoinEffectDelegateImpl<Effect>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { controllerProvider.invoke() } returns controller
        every { controller.effectImplementation } returns expectedEffect
        delegate = KoinEffectDelegateImpl(
            controllerProvider = controllerProvider
        )
    }

    @Test
    fun `verify lazy initialization`() {
        verify(exactly = 0) {
            controllerProvider.invoke()
        }

        val effect1 by delegate
        val effect2 by delegate

        assertSame(effect1, effect2)
        verify(exactly = 1) {
            controllerProvider.invoke()
        }
    }

    @Test
    fun `getValue() returns effect implementation from controller`() {
        val effect by delegate
        assertSame(this.expectedEffect, effect)
    }

    @Test
    fun `onStart() starts controller`() {
        delegate.onStart(lifecycleOwner)

        verify(exactly = 1) {
            controller.start()
        }
    }

    @Test
    fun `onStop() stops controller`() {
        delegate.onStop(lifecycleOwner)

        verify(exactly = 1) {
            controller.stop()
        }
    }

    private interface Effect
}