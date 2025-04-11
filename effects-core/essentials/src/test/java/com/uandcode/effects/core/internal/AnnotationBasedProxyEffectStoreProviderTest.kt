package com.uandcode.effects.core.internal

import com.uandcode.effects.core.GeneratedProxyEffectStoreProvider
import com.uandcode.effects.core.kspcontract.AnnotationBasedProxyEffectStore
import org.junit.Assert.assertSame
import org.junit.Test

class AnnotationBasedProxyEffectStoreProviderTest {

    @Test
    fun `test getGeneratedProxyEffectStore`() {
        val actualStore = AnnotationBasedProxyEffectStore

        val resultStore = GeneratedProxyEffectStoreProvider.getGeneratedProxyEffectStore()

        assertSame(actualStore, resultStore)
    }
}