package com.uandcode.effects.core.internal.scopes

import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.core.createChild
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.getController
import com.uandcode.effects.core.runtime.RuntimeProxyEffectFactory
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultEffectScopeTest {

    private lateinit var effectScope: DefaultEffectScope

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        effectScope = DefaultEffectScope(
            managedInterfaces = ManagedInterfaces.ListOf(Effect1::class),
            proxyEffectFactory = RuntimeProxyEffectFactory(),
            parent = null,
        )
    }

    @Test
    fun `getProxy() with unknown effect throws EffectNotFoundException`() {
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
    fun `getProxy() returns proxy implementation`() {
        val effect: Effect1 = effectScope.getProxy(Effect1::class)
        assertNotNull(effect)
    }

    @Test
    fun `test getController()`() {
        val effect = effectScope.getProxy(Effect1::class)
        val effectImpl = mockk<Effect1>(relaxUnitFun = true)

        val controller = effectScope.getController(Effect1::class)
        effect.run1()
        controller.start(effectImpl)

        verify(exactly = 1) { effectImpl.run1() }
    }

    @Test
    fun `getProxy() in child scope can return 2 implementations`() {
        val childEffectScope = effectScope.createChild(
            ManagedInterfaces.ListOf(Effect2::class),
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
            ManagedInterfaces.ListOf(Effect2::class)
        )

        val effect1 = childEffectScope.getProxy(Effect1::class)
        val effect2 = childEffectScope.getProxy(Effect2::class)
        val controller1 = childEffectScope.getController(Effect1::class)
        val controller2 = childEffectScope.getController(Effect2::class)

        effect1.run1()
        controller1.start(effect1Impl)
        verify {
            effect1Impl.run1()
            effect2Impl wasNot called
        }

        effect2.run2()
        controller2.start(effect2Impl)
        verify(exactly = 1) {
            effect2Impl.run2()
        }
    }

    @Test
    fun `test getController() with combined effect implementation`() {
        val effectScope = DefaultEffectScope(
            managedInterfaces = ManagedInterfaces.ListOf(Effect1::class, Effect2::class),
            proxyEffectFactory = RuntimeProxyEffectFactory(),
            parent = null,
        )
        val proxy1 = effectScope.getProxy(Effect1::class)
        val proxy2 = effectScope.getProxy(Effect2::class)
        val combinedEffect = spyk(CombinedEffect())
        val controller = effectScope.getController(CombinedEffect::class)
        controller.start(combinedEffect)

        proxy1.run1()
        verify(exactly = 1) {
            combinedEffect.run1()
        }

        proxy2.run2()
        verify(exactly = 1) {
            combinedEffect.run2()
        }
    }

    @Test
    fun `getController() without all available interfaces for combined effect fails`() {
        val effectScope = DefaultEffectScope(
            managedInterfaces = ManagedInterfaces.ListOf(Effect1::class),
            proxyEffectFactory = RuntimeProxyEffectFactory(),
            parent = null,
        )

        val result = runCatching {
            effectScope.getController(CombinedEffect::class)
        }

        assertTrue(result.exceptionOrNull() is EffectNotFoundException)
    }

    @Test
    fun `test getController() with combined effect implementation from different scopes`() {
        val childScope = effectScope.createChild(
            ManagedInterfaces.ListOf(Effect2::class)
        )
        val proxy1 = childScope.getProxy(Effect1::class)
        val proxy2 = childScope.getProxy(Effect2::class)
        val combinedEffect = spyk(CombinedEffect())
        val controller = childScope.getController(CombinedEffect::class)
        controller.start(combinedEffect)

        proxy1.run1()
        verify(exactly = 1) {
            combinedEffect.run1()
        }

        proxy2.run2()
        verify(exactly = 1) {
            combinedEffect.run2()
        }
    }

    interface Effect1 {
        fun run1()
    }

    interface Effect2 {
        fun run2()
    }

    interface Effect3 {
        fun run2()
    }

    class CombinedEffect : Effect1, Effect2 {
        override fun run1() = Unit
        override fun run2() = Unit
    }

}