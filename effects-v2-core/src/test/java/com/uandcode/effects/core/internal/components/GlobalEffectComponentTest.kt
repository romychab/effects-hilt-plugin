package com.uandcode.effects.core.internal.components

import com.uandcode.effects.stub.GeneratedProxyEffectStore
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test

class GlobalEffectComponentTest {

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test GlobalEffectComponent is DefaultEffectComponent`() {
        mockkObject(GeneratedProxyEffectStore)
        val expectedTargetInterfaces = setOf(Effect::class)
        every { GeneratedProxyEffectStore.allTargetInterfaces } returns expectedTargetInterfaces
        every { GeneratedProxyEffectStore.createProxy(Effect::class, any()) } returns ProxyEffectImpl()

        val globalComponent = buildGlobalEffectComponent()

        assertTrue(globalComponent is DefaultEffectComponent)
        val proxyEffect = globalComponent.get(Effect::class)
        assertTrue(proxyEffect is ProxyEffectImpl)
        unmockkObject(GeneratedProxyEffectStore)
    }

    private interface Effect
    private class ProxyEffectImpl : Effect
}