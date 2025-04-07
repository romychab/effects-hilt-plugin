package com.uandcode.effects.core.internal.scopes

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

class DefaultEffectScopeTest {

    private lateinit var effectScope: DefaultEffectScope

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        effectScope = DefaultEffectScope(
            interfaces = EffectInterfaces.ListOf(Effect1::class),
            proxyEffectFactory = RuntimeProxyEffectFactory(),
            parent = null,
        )
    }

    @Test
    fun `get() with unknown effect throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            effectScope.getProxy(Effect2::class)
        }
    }

    @Test
    fun `getController() with unknown effect throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            effectScope.getController(Effect2::class)
        }
    }

    @Test
    fun `get() returns proxy implementation`() {
        val effect: Effect1 = effectScope.getProxy(Effect1::class)
        assertNotNull(effect)
    }

    @Test
    fun `test getController()`() {
        val effect = effectScope.getProxy(Effect1::class)
        val effectImpl = mockk<Effect1>(relaxUnitFun = true)

        val controller = effectScope.getController(Effect1::class)
        effect.run()
        controller.start(effectImpl)

        verify(exactly = 1) { effectImpl.run() }
    }

    @Test
    fun `get() in child scope can return 2 implementations`() {
        val childEffectScope = effectScope.createChild(
            EffectInterfaces.ListOf(Effect2::class),
        )

        val effect1 = childEffectScope.getProxy(Effect1::class)
        val effect2 = childEffectScope.getProxy(Effect2::class)

        assertNotNull(effect1)
        assertNotNull(effect2)
    }

    @Test
    fun `getController() in child scope can return controllers for 2 implementations`() {
        val effect1Impl = mockk<Effect1>(relaxUnitFun = true)
        val effect2Impl = mockk<Effect2>(relaxUnitFun = true)
        val childEffectScope = effectScope.createChild(
            EffectInterfaces.ListOf(Effect2::class)
        )

        val effect1 = childEffectScope.getProxy(Effect1::class)
        val effect2 = childEffectScope.getProxy(Effect2::class)
        val controller1 = childEffectScope.getController(Effect1::class)
        val controller2 = childEffectScope.getController(Effect2::class)

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