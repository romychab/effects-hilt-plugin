package com.uandcode.effects.core.internal

import com.uandcode.effects.core.EffectController
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BoundEffectImplControllerTest {

    @RelaxedMockK
    private lateinit var controller: EffectController<String>

    @MockK
    private lateinit var provider: () -> String

    private lateinit var boundEffectController: BoundEffectControllerImpl<String>

    private val effect: String = "effect"

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { provider() } returns effect
        boundEffectController = BoundEffectControllerImpl(
            controller = controller,
            effectImplementationProvider = provider
        )
    }

    @Test
    fun `effectImplementation should be lazily initialized`() {
        verify {
            provider wasNot called
        }
        boundEffectController.effectImplementation
        verify(exactly = 1) {
            provider.invoke()
        }
    }

    @Test
    fun `isStarted should delegate call to origin controller`() {
        every { controller.isStarted } returns true
        assertTrue(boundEffectController.isStarted)

        every { controller.isStarted } returns false
        assertFalse(boundEffectController.isStarted)
    }

    @Test
    fun `start should call start on controller with effectImplementation`() {
        boundEffectController.start()

        verify(exactly = 1) {
            controller.start(effect)
        }
    }

    @Test
    fun `stop should call stop on controller`() {
        boundEffectController.stop()

        verify(exactly = 1) {
            controller.stop()
        }
    }

}
