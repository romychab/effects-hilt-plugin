package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.internal.ProxyEffectStoreProvider
import com.uandcode.effects.stub.api.ProxyConfiguration
import com.uandcode.effects.stub.api.ProxyEffectStore
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultEffectComponentTest {

    @MockK
    private lateinit var proxyEffectStore: ProxyEffectStore

    private lateinit var effectComponent: DefaultEffectComponent

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        effectComponent = DefaultEffectComponent(setOf(Effect1::class))
        mockkObject(ProxyEffectStoreProvider)
        every { ProxyEffectStoreProvider.getGeneratedProxyEffectStore() } returns proxyEffectStore
        every { proxyEffectStore.proxyConfiguration } returns ProxyConfiguration()
        every { proxyEffectStore.createProxy(Effect1::class, any()) } answers { ProxyEffect1(secondArg()) }
        every { proxyEffectStore.createProxy(Effect2::class, any()) } answers { ProxyEffect2(secondArg()) }
        every { proxyEffectStore.findTargetInterface(Effect1::class) } returns Effect1::class
        every { proxyEffectStore.findTargetInterface(Effect1Impl::class) } returns Effect1::class
        every { proxyEffectStore.findTargetInterface(Effect2::class) } returns Effect2::class
        every { proxyEffectStore.findTargetInterface(Effect2Impl::class) } returns Effect2::class
    }

    @After
    fun tearDown() {
        unmockkObject(ProxyEffectStoreProvider)
    }

    @Test
    fun `get() with unknown effect throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            effectComponent.get(Effect2::class)
        }
    }

    @Test
    fun `getController() with unknown effect throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            effectComponent.getController(Effect2::class)
        }
    }

    @Test
    fun `getBoundController() with unknown effect throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            effectComponent.getBoundController(Effect2::class) { mockk() }
        }
    }

    @Test
    fun `get() returns proxy implementation`() {
        val effect = effectComponent.get(Effect1::class)

        assertTrue(effect is ProxyEffect1)
    }

    @Test
    fun `test getController()`() {
        val effectImpl = Effect1Impl()
        val effect = effectComponent.get(Effect1::class)

        val controller = effectComponent.getController(Effect1::class)

        effect.update()
        controller.start(effectImpl)
        assertTrue(effectImpl.updated)
    }

    @Test
    fun `test getBoundController()`() {
        val effectImpl = Effect1Impl()
        val effect = effectComponent.get(Effect1::class)

        val controller = effectComponent.getBoundController(Effect1::class) {
            effectImpl
        }

        effect.update()
        assertFalse(effectImpl.updated)
        controller.start()
        assertTrue(effectImpl.updated)
    }

    @Test
    fun `get() in child component can return 2 implementations`() {
        val childEffectComponent = effectComponent.createChild(Effect2::class)

        val effect1 = childEffectComponent.get(Effect1::class)
        val effect2 = childEffectComponent.get(Effect2::class)

        assertTrue(effect1 is ProxyEffect1)
        assertTrue(effect2 is ProxyEffect2)
    }

    @Test
    fun `getController() in child component can return controllers for 2 implementations`() {
        val effect1Impl = Effect1Impl()
        val effect2Impl = Effect2Impl()
        val childEffectComponent = effectComponent.createChild(Effect2::class)

        val effect1 = childEffectComponent.get(Effect1::class)
        val effect2 = childEffectComponent.get(Effect2::class)
        val controller1 = childEffectComponent.getController(Effect1::class)
        val controller2 = childEffectComponent.getController(Effect2::class)

        effect1.update()
        controller1.start(effect1Impl)
        assertTrue(effect1Impl.updated)
        assertFalse(effect2Impl.updated)

        effect2.update()
        controller2.start(effect2Impl)
        assertTrue(effect2Impl.updated)
    }

    private interface Effect1 {
        fun update()
    }

    private interface Effect2 {
        fun update()
    }

    private class ProxyEffect1(val commandExecutor: CommandExecutor<Effect1>) : Effect1 {
        override fun update() {
            commandExecutor.execute { it.update() }
        }
    }

    private class ProxyEffect2(val commandExecutor: CommandExecutor<Effect2>) : Effect2 {
        override fun update() {
            commandExecutor.execute { it.update() }
        }
    }

    private class Effect1Impl : Effect1 {
        var updated = false
        override fun update() { updated = true }
    }

    private class Effect2Impl : Effect2 {
        var updated = false
        override fun update() { updated = true }
    }

}