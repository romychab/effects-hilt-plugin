package com.uandcode.effects.core.internal

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class InternalProxyEffectStoreImplTest {

    private lateinit var proxyEffectStore: InternalProxyEffectStoreImpl

    @MockK
    private lateinit var commandExecutor: CommandExecutor<Effect>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        proxyEffectStore = InternalProxyEffectStoreImpl()
    }

    @Test
    fun `test registerProxyProvider and createProxy`() {
        proxyEffectStore.registerProxyProvider(
            Effect::class,
            InternalProxyEffectStoreImplTest::ProxyEffectImpl
        )

        val createdProxy = proxyEffectStore
            .createProxy(Effect::class, commandExecutor) as ProxyEffectImpl

        assertSame(commandExecutor, createdProxy.commandExecutor)
    }

    @Test
    fun `test createProxy throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            proxyEffectStore.createProxy(Effect::class, commandExecutor)
        }
    }

    @Test
    fun `test registerTarget and findTargetInterfaces`() {
        proxyEffectStore.registerTarget(EffectImpl::class, Effect::class)

        val targetInterfaces1 = proxyEffectStore.findTargetInterfaces(EffectImpl::class)
        val targetInterfaces2 = proxyEffectStore.findTargetInterfaces(Effect::class)

        assertEquals(setOf(Effect::class), targetInterfaces1)
        assertEquals(setOf(Effect::class), targetInterfaces2)
    }

    @Test
    fun `test findTargetInterfaces throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            proxyEffectStore.findTargetInterfaces(EffectImpl::class)
        }
    }

    @Test
    fun `test allTargetInterfaces`() {
        proxyEffectStore.registerTarget(EffectImpl::class, Effect::class)

        val allTargets = proxyEffectStore.allTargetInterfaces

        assertEquals(setOf(Effect::class), allTargets)
    }

    @Test
    fun `findTargetInterfaces for combined class returns all interfaces`() {
        proxyEffectStore.registerTarget(CombinedEffect::class, Effect1::class)
        proxyEffectStore.registerTarget(CombinedEffect::class, Effect2::class)

        val interfaces = proxyEffectStore.findTargetInterfaces(CombinedEffect::class)
        assertEquals(setOf(Effect1::class, Effect2::class), interfaces)

        val effect1 = proxyEffectStore.findTargetInterfaces(Effect1::class)
        assertEquals(setOf(Effect1::class), effect1)

        val effect2 = proxyEffectStore.findTargetInterfaces(Effect2::class)
        assertEquals(setOf(Effect2::class), effect2)
    }

    private interface Effect
    private class EffectImpl : Effect
    private class ProxyEffectImpl(
        val commandExecutor: CommandExecutor<Effect>,
    ) : Effect

    private interface Effect1
    private interface Effect2
    private class CombinedEffect : Effect1, Effect2

}