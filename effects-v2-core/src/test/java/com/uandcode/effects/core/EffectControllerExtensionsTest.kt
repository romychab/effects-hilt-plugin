package com.uandcode.effects.core

import com.uandcode.effects.core.exceptions.ControllerAlreadyStartedException
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class EffectControllerExtensionsTest {

    @Test(expected = ControllerAlreadyStartedException::class)
    fun `bind should throw ControllerAlreadyStartedException if controller is already started`() {
        val effectController = mockk<EffectController<Any>>()
        every { effectController.isStarted } returns true

        effectController.bind { Any() }
    }

    @Test
    fun `bind should return BoundEffectController if controller is not started`() {
        val effectController = mockk<EffectController<Any>>()
        every { effectController.isStarted } returns false
        val provider = { Any() }

        val boundEffectController = effectController.bind(provider)

        assertNotNull(boundEffectController)
        assertFalse(boundEffectController.isStarted)
    }

}
