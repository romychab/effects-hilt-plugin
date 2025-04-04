package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.EffectInterfaces
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.runtime.RuntimeProxyEffectFactory
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class DefaultEffectComponentTest {

    private lateinit var effectComponent: DefaultEffectComponent

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        effectComponent = DefaultEffectComponent(
            interfaces = EffectInterfaces.ListOf(Effect1::class),
            proxyEffectFactory = RuntimeProxyEffectFactory(),
            parent = null,
        )
    }

    @Test
    fun `get() with unknown effect throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            effectComponent.getProxy(Effect2::class)
        }
    }

    @Test
    fun `getController() with unknown effect throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            effectComponent.getController(Effect2::class)
        }
    }

    @Test
    fun `get() returns proxy implementation`() {
        val effect: Effect1 = effectComponent.getProxy(Effect1::class)
        assertNotNull(effect)
    }

    @Test
    fun `test getController()`() {
        val effect = effectComponent.getProxy(Effect1::class)
        val effectImpl = mockk<Effect1>(relaxUnitFun = true)

        val controller = effectComponent.getController(Effect1::class)
        effect.run()
        controller.start(effectImpl)

        verify(exactly = 1) { effectImpl.run() }
    }

    @Test
    fun `get() in child component can return 2 implementations`() {
        val childEffectComponent = effectComponent.createChild(
            EffectInterfaces.ListOf(Effect2::class),
        )

        val effect1 = childEffectComponent.getProxy(Effect1::class)
        val effect2 = childEffectComponent.getProxy(Effect2::class)

        assertNotNull(effect1)
        assertNotNull(effect2)
    }

    @Test
    fun `getController() in child component can return controllers for 2 implementations`() {
        val effect1Impl = mockk<Effect1>(relaxUnitFun = true)
        val effect2Impl = mockk<Effect2>(relaxUnitFun = true)
        val childEffectComponent = effectComponent.createChild(
            EffectInterfaces.ListOf(Effect2::class)
        )

        val effect1 = childEffectComponent.getProxy(Effect1::class)
        val effect2 = childEffectComponent.getProxy(Effect2::class)
        val controller1 = childEffectComponent.getController(Effect1::class)
        val controller2 = childEffectComponent.getController(Effect2::class)

        effect1.run()
        controller1.start(effect1Impl)
        verify {
            effect1Impl.run()
            effect2Impl wasNot called
        }

        effect2.run()
        controller2.start(effect2Impl)
        verify(exactly = 1) {
            effect2Impl.run()
        }
    }

    interface Effect1 {
        fun run()
    }

    interface Effect2 {
        fun run()
    }

}