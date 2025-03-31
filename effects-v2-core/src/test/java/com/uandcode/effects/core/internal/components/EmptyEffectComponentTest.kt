package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.exceptions.EffectNotFoundException
import org.junit.Assert.assertThrows
import org.junit.Test

class EmptyEffectComponentTest {

    @Test
    fun `get() throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            EmptyEffectComponent.get(Any::class)
        }
    }

    @Test
    fun `getController() throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            EmptyEffectComponent.getController(Any::class)
        }
    }

    @Test
    fun `getBoundController() throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            EmptyEffectComponent.getBoundController(Any::class) { Any() }
        }
    }

    @Test
    fun `cleanUp() does not throw any exception`() {
        EmptyEffectComponent.cleanUp()
    }
}