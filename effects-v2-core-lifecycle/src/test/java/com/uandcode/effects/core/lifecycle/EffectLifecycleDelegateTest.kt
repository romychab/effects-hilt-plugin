package com.uandcode.effects.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.lifecycle.internal.EffectLifecycleDelegateImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EffectLifecycleDelegateTest {

    @MockK(relaxUnitFun = true)
    private lateinit var controller: BoundEffectController<Any>

    @MockK
    private lateinit var lifecycleOwner: LifecycleOwner

    @MockK
    private lateinit var lifecycle: Lifecycle

    private lateinit var delegate: EffectLifecycleDelegateImpl<Any>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { lifecycleOwner.lifecycle } returns lifecycle
        every { lifecycle.addObserver(any()) } just runs
        delegate = EffectLifecycleDelegateImpl(lifecycleOwner, controller)
    }

    @Test
    fun `delegate is registered after instantiation`() {
        verify(exactly = 1) {
            lifecycle.addObserver(delegate)
        }
    }

    @Test
    fun `getValue() returns effect implementation`() {
        val expectedEffect = "effect"
        every { controller.effectImplementation } returns expectedEffect

        val effect = delegate.getValue(null, mockk())

        assertEquals(expectedEffect, effect)
    }

    @Test
    fun `onStart() calls controller start`() {
        delegate.onStart(lifecycleOwner)

        verify(exactly = 1) { controller.start() }
    }

    @Test
    fun `onStop() calls controller stop`() {
        delegate.onStop(lifecycleOwner)

        verify(exactly = 1) { controller.stop() }
    }

}