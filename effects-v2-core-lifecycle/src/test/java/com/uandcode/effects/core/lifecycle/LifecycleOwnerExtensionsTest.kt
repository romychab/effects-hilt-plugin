package com.uandcode.effects.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.EffectComponent
import com.uandcode.effects.core.RootEffectComponents
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class LifecycleOwnerExtensionsTest {

    @MockK
    private lateinit var lifecycleOwner: LifecycleOwner

    @MockK
    private lateinit var lifecycle: Lifecycle

    @MockK
    private lateinit var effectComponent: EffectComponent

    @MockK
    private lateinit var boundEffectController: BoundEffectController<String>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { lifecycleOwner.lifecycle } returns lifecycle
        every { lifecycle.addObserver(any()) } just runs
        every {
            effectComponent.getBoundController(String::class, any())
        } returns boundEffectController
    }

    @After
    fun tearDown() {
        RootEffectComponents.resetComponents()
    }

    @Test
    fun `lazyEffect creates LazyEffectDelegate`() {
        val provider = { "effect" }
        val lazyEffectDelegate = lifecycleOwner.lazyEffect(effectComponent, provider)

        assertNotNull(lazyEffectDelegate)
        verify(exactly = 1) {
            effectComponent.getBoundController(String::class, provider)
        }
    }

    @Test
    fun `lazyEffect uses default component`() {
        RootEffectComponents.setGlobal(effectComponent)
        val provider = { "effect" }
        val lazyEffectDelegate = lifecycleOwner.lazyEffect(provider = provider)

        assertNotNull(lazyEffectDelegate)
        verify(exactly = 1) {
            effectComponent.getBoundController(String::class, provider)
        }
    }

}
