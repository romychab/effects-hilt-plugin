package com.uandcode.effects.core.internal

import com.uandcode.effects.stub.GeneratedProxyEffectStore
import org.junit.Assert.assertSame
import org.junit.Test

class ProxyEffectStoreProviderTest {

    @Test
    fun `test getGeneratedProxyEffectStore`() {
        val actualStore = GeneratedProxyEffectStore

        val resultStore = ProxyEffectStoreProvider.getGeneratedProxyEffectStore()

        assertSame(actualStore, resultStore)
    }
}