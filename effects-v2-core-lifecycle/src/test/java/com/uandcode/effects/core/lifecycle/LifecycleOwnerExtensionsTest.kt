package com.uandcode.effects.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.EffectComponent
import com.uandcode.effects.core.RootEffectComponents
import com.uandcode.effects.core.getBoundController
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
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
    private lateinit var controller: BoundEffectController<String>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        RootEffectComponents.setGlobal(effectComponent)
        every { lifecycleOwner.lifecycle } returns lifecycle
        every { lifecycle.addObserver(any()) } just runs
        every {
            effectComponent.getBoundController(String::class, any())
        } answers {
            val provider: () -> String = secondArg()
            every { controller.effectImplementation } returns provider()
            controller
        }
    }

    @After
    fun tearDown() {
        RootEffectComponents.resetComponents()
    }

    @Test
    fun `lazyEffect uses default component`() {
        val expectedEffect = "effect-implementation"
        val provider = { expectedEffect }
        val lazyEffectDelegate = lifecycleOwner.lazyEffect(effectProvider = provider)

        val effect by lazyEffectDelegate

        assertEquals(expectedEffect, effect)
        assertNotNull(lazyEffectDelegate)
        verify(exactly = 1) {
            effectComponent.getBoundController(String::class, provider)
        }
    }

    @Test
    fun `lazyEffect initializes controller lazily`() {
        val expectedEffect = "effect-implementation"
        val provider = mockk<() -> String>()
        every { provider.invoke() } returns expectedEffect
        val lazyEffectDelegate = lifecycleOwner.lazyEffect(effectProvider = provider)

        val effect by lazyEffectDelegate

        // verify nothing happened
        verify {
            provider wasNot called
        }
        verify(exactly = 0) {
            effectComponent.getBoundController(String::class, any())
        }
        // 1. read effect variable and verify the initialization
        // 2. read effect again and verify that the initialization is not repeated
        repeat(2) {
            assertEquals(expectedEffect, effect) // <-- read effect variable
            verify(exactly = 1) {
                provider.invoke()
            }
            verify(exactly = 1) {
                effectComponent.getBoundController(String::class, provider)
            }
        }
    }

}
