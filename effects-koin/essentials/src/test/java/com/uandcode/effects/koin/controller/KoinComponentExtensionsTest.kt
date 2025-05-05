package com.uandcode.effects.koin.controller

import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.getController
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class KoinComponentExtensionsTest {

    @MockK
    private lateinit var effectScope: EffectScope

    @MockK
    private lateinit var koinComponent: KoinComponent

    @MockK
    private lateinit var expectedController: EffectController<Effect>

    private lateinit var expectedEffectImpl: EffectImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        expectedEffectImpl = EffectImpl()
        every { koinComponent.get<EffectScope>() } returns effectScope
        every { effectScope.getController<Effect>() } returns expectedController
        every { expectedController.isStarted } returns false
    }

    @Test
    fun `test getEffectController`() {
        val controller = koinComponent.getEffectController<Effect>()
        assertSame(expectedController, controller)
    }

    @Test
    fun `test injectEffectController`() {
        val lazy = koinComponent.injectEffectController<Effect>()
        val controller = lazy.value
        assertSame(expectedController, controller)
    }

    @Test
    fun `test getBoundEffectController`() {
        val boundController = koinComponent.getBoundEffectController<Effect> { expectedEffectImpl }
        assertSame(expectedEffectImpl, boundController.effectImplementation)
    }

    @Test
    fun `test injectBoundEffectController`() {
        val lazy = koinComponent.injectBoundEffectController<Effect> { expectedEffectImpl }
        val boundController = lazy.value
        assertSame(expectedEffectImpl, boundController.effectImplementation)
    }

    private interface Effect
    private class EffectImpl : Effect

}
