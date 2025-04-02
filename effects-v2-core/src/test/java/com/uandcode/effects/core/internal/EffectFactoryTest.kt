package com.uandcode.effects.core.internal

import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.ObservableResourceStore
import com.uandcode.effects.stub.api.ProxyEffectStore
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EffectFactoryTest {

    @MockK
    private lateinit var proxyEffectStore: ProxyEffectStore

    @RelaxedMockK
    private lateinit var resourceStore: ObservableResourceStore<Effect>

    private lateinit var effectFactory: EffectFactory<Effect>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(ProxyEffectStoreProvider)
        every { ProxyEffectStoreProvider.getGeneratedProxyEffectStore() } returns proxyEffectStore
        effectFactory = EffectFactory(Effect::class, resourceStore)
    }

    @After
    fun tearDown() {
        unmockkObject(ProxyEffectStoreProvider)
    }

    @Test
    fun `test provideProxy`() {
        val expectedEffect = ProxyEffectImpl()
        every {
            proxyEffectStore.createProxy<Effect>(any(), any())
        } returns expectedEffect

        val resultEffect = effectFactory.provideProxy()

        assertSame(expectedEffect, resultEffect)
        verify(exactly = 1) {
            proxyEffectStore.createProxy(Effect::class, any())
        }
    }

    @Test
    fun `test createController`() {
        val controller: EffectController<EffectImpl> = effectFactory.createController()

        assertTrue(controller is EffectControllerImpl)
    }

    @Test
    fun `test createBoundController`() {
        val boundImplementationProvider = mockk<() -> EffectImpl>()

        val boundController = effectFactory.createBoundController(boundImplementationProvider)

        assertTrue(boundController is BoundEffectControllerImpl)
        verify { boundImplementationProvider wasNot called }
    }

    @Test
    fun `test cleanUp`() {
        effectFactory.cleanUp()

        verify(exactly = 1) { resourceStore.removeAllObservers() }
    }

    private interface Effect
    private class EffectImpl : Effect
    private class ProxyEffectImpl : Effect

}