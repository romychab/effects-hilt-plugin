package com.uandcode.effects.core.internal.scopes

import com.uandcode.effects.core.GeneratedProxyEffectStoreProvider
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

class GlobalEffectScopeTest {

    @MockK
    private lateinit var proxyEffectStore: ProxyEffectStore

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(GeneratedProxyEffectStoreProvider)
        every { GeneratedProxyEffectStoreProvider.getGeneratedProxyEffectStore() } returns proxyEffectStore
    }

    @After
    fun tearDown() {
        unmockkObject(GeneratedProxyEffectStoreProvider)
    }

    @Test
    fun `test GlobalEffectScope is LazyEffectScope`() {
        val expectedTargetInterfaces = setOf(Effect::class)
        every { proxyEffectStore.allTargetInterfaces } returns expectedTargetInterfaces
        every { proxyEffectStore.createProxy(Effect::class, any()) } returns ProxyEffectImpl()
        every { proxyEffectStore.findTargetInterfaces(Effect::class) } returns setOf(Effect::class)

        val globalScope = buildGlobalEffectScope()

        assertTrue(globalScope is LazyEffectScope)
        val proxyEffect = globalScope.getProxy(Effect::class)
        assertTrue(proxyEffect is ProxyEffectImpl)
    }

    private interface Effect
    private class ProxyEffectImpl : Effect

}
