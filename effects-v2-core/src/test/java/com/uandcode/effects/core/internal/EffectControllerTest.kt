package com.uandcode.effects.core.internal

import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.ObservableResourceStore
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class EffectControllerTest {

    @RelaxedMockK
    private lateinit var observableResourceStore: ObservableResourceStore<String>

    private lateinit var effectController: EffectController<String>

    private val effect1: String = "effect1"
    private val effect2: String = "effect2"
    private val effect: String = effect1

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        effectController = EffectControllerImpl(observableResourceStore)
    }

    @Test
    fun `start with valid effect should attach resource`() {
        effectController.start(effect)

        assertEquals(effect, effectController.effectImplementation)

        verify(exactly = 1) {
            observableResourceStore.attachResource(effect)
        }
    }

    @Test
    fun `start when already started does nothing`() {
        effectController.start(effect1)
        effectController.start(effect2)

        assertEquals(effect1, effectController.effectImplementation)
        verify(exactly = 1) {
            observableResourceStore.attachResource(effect1)
        }
        verify(exactly = 0) {
            observableResourceStore.attachResource(effect2)
        }
    }

    @Test
    fun `stop with started effect detaches resource`() {
        effectController.start(effect)

        effectController.stop()

        assertNull(effectController.effectImplementation)
        verify(exactly = 1) {
            observableResourceStore.detachResource(effect)
        }
    }

    @Test
    fun `stop when not started does nothing`() {
        effectController.stop()

        assertNull(effectController.effectImplementation)
        verify(exactly = 0) {
            observableResourceStore.detachResource(any())
        }
    }

}