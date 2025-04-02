package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.internal.ProxyEffectStoreProvider
import com.uandcode.effects.stub.api.ProxyEffectStore
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GlobalEffectComponentTest {

    @MockK
    private lateinit var proxyEffectStore: ProxyEffectStore

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(ProxyEffectStoreProvider)
        every { ProxyEffectStoreProvider.getGeneratedProxyEffectStore() } returns proxyEffectStore
    }

    @After
    fun tearDown() {
        unmockkObject(ProxyEffectStoreProvider)
    }

    @Test
    fun `test GlobalEffectComponent is DefaultEffectComponent`() {
        val expectedTargetInterfaces = setOf(Effect::class)
        every { proxyEffectStore.allTargetInterfaces } returns expectedTargetInterfaces
        every { proxyEffectStore.createProxy(Effect::class, any()) } returns ProxyEffectImpl()

        val globalComponent = buildGlobalEffectComponent()

        assertTrue(globalComponent is DefaultEffectComponent)
        val proxyEffect = globalComponent.get(Effect::class)
        assertTrue(proxyEffect is ProxyEffectImpl)
    }

    private interface Effect
    private class ProxyEffectImpl : Effect
}