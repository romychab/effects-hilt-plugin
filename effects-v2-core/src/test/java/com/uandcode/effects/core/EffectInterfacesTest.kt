package com.uandcode.effects.core

import com.uandcode.effects.core.factories.ProxyEffectFactory
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class EffectInterfacesTest {

    @Test
    fun `Everything instance creates EffectManager for any class`() {
        val factory = mockk<ProxyEffectFactory>()
        val expectedProxy = "proxy"
        every { factory.createProxy(String::class, any()) } returns expectedProxy

        val manager = EffectInterfaces.Everything.createEffectManager(
            String::class,
            factory,
        )

        assertNotNull(manager)
        val resultProxy = manager.provideProxy()
        assertEquals(expectedProxy, resultProxy)
    }

    @Test
    fun `ListOf instance creates EffectManager for classes in the list`() {
        val listOf = EffectInterfaces.ListOf(Int::class, String::class)
        val factory = mockk<ProxyEffectFactory>()
        val expectedProxy = "proxy"
        every { factory.createProxy(String::class, any()) } returns expectedProxy

        val manager = listOf.createEffectManager(String::class, factory)

        assertNotNull(manager)
        val resultProxy = manager?.provideProxy()
        assertEquals(expectedProxy, resultProxy)
    }

    @Test
    fun `ListOf instance does not create EffectManager for classes not in the list`() {
        val listOf = EffectInterfaces.ListOf(String::class)

        val manager = listOf.createEffectManager(Int::class, mockk())

        assertNull(manager)
    }

}