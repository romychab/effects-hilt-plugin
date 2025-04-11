package com.uandcode.effects.core.factories

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.stub.api.ProxyConfiguration
import com.uandcode.effects.stub.api.ProxyEffectStore
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class DefaultProxyEffectFactoryTest {

    @MockK
    private lateinit var mockProxyEffectStore: ProxyEffectStore

    private lateinit var factory: DefaultProxyEffectFactory

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        factory = DefaultProxyEffectFactory(proxyEffectStore = mockProxyEffectStore)
    }

    @Test
    fun `test findTargetInterfaces`() {
        val testClass = TestClass::class
        val expectedInterfaces = setOf(Interface1::class, Interface2::class)
        every { mockProxyEffectStore.findTargetInterfaces(testClass) } returns expectedInterfaces

        val result = factory.findTargetInterfaces(testClass)

        assertEquals(expectedInterfaces, result)
    }

    @Test
    fun `test createProxy`() {
        val testClass = TestClass::class
        val mockCommandExecutor = mockk<CommandExecutor<TestClass>>()
        val mockProxyInstance = mockk<TestClass>()
        every { mockProxyEffectStore.createProxy(testClass, mockCommandExecutor) } returns mockProxyInstance

        val result = factory.createProxy(testClass, mockCommandExecutor)

        assertSame(mockProxyInstance, result)
    }

    @Test
    fun `test proxyConfiguration`() {
        val mockProxyConfiguration = mockk<ProxyConfiguration>()
        every { mockProxyEffectStore.proxyConfiguration } returns mockProxyConfiguration

        val result = factory.proxyConfiguration

        assertSame(mockProxyConfiguration, result)
    }

    private interface Interface1
    private interface Interface2
    private class TestClass : Interface1, Interface2
}