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
import org.koin.core.scope.Scope

class ScopeExtensionsTest {

    @MockK
    private lateinit var effectScope: EffectScope

    @MockK
    private lateinit var scope: Scope

    @MockK
    private lateinit var expectedController: EffectController<Effect>

    private lateinit var expectedEffectImpl: EffectImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        expectedEffectImpl = EffectImpl()
        every { scope.get<EffectScope>() } returns effectScope
        every { effectScope.getController<Effect>() } returns expectedController
        every { expectedController.isStarted } returns false
    }

    @Test
    fun `test getEffectController`() {
        val controller = scope.getEffectController<Effect>()
        assertSame(expectedController, controller)
    }

    @Test
    fun `test injectEffectController`() {
        val lazy = scope.injectEffectController<Effect>()
        val controller = lazy.value
        assertSame(expectedController, controller)
    }

    @Test
    fun `test getBoundEffectController`() {
        val boundController = scope.getBoundEffectController<Effect> { expectedEffectImpl }
        assertSame(expectedEffectImpl, boundController.effectImplementation)
    }

    @Test
    fun `test injectBoundEffectController`() {
        val lazy = scope.injectBoundEffectController<Effect> { expectedEffectImpl }
        val boundController = lazy.value
        assertSame(expectedEffectImpl, boundController.effectImplementation)
    }

    private interface Effect
    private class EffectImpl : Effect

}
