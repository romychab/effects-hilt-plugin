package com.uandcode.effects.core

import com.uandcode.effects.core.internal.scopes.buildGlobalEffectScope
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class RootEffectScopesTest {

    @MockK
    private lateinit var effectScope: EffectScope

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test default global scope`() {
        val expectedScope = mockk<EffectScope>()
        val expectedProxy = "proxy"
        mockkStatic(::buildGlobalEffectScope)
        every { buildGlobalEffectScope() } returns expectedScope
        every { expectedScope.getProxy(String::class) } returns expectedProxy
        RootEffectScopes.resetGlobalScope()

        val globalScope = RootEffectScopes.global

        assertSame(expectedScope, globalScope)
    }

    @Test
    fun `test set custom global scope`() {
        RootEffectScopes.setGlobal(effectScope)
        assertEquals(effectScope, RootEffectScopes.global)
    }

    @Test
    fun `test reset scope`() {
        val expectedScope = mockk<EffectScope>(relaxUnitFun = true)
        mockkStatic(::buildGlobalEffectScope)
        every { buildGlobalEffectScope() } returns expectedScope

        RootEffectScopes.setGlobal(effectScope)
        RootEffectScopes.resetGlobalScope()

        assertSame(expectedScope, RootEffectScopes.global)
    }

}
