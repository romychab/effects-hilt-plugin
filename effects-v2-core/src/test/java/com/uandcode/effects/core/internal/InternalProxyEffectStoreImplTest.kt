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
    fun `test registerTarget and findTargetInterface`() {
        proxyEffectStore.registerTarget(EffectImpl::class, Effect::class)

        val targetInterface1 = proxyEffectStore.findTargetInterface(EffectImpl::class)
        val targetInterface2 = proxyEffectStore.findTargetInterface(Effect::class)

        assertEquals(Effect::class, targetInterface1)
        assertEquals(Effect::class, targetInterface2)
    }

    @Test
    fun `test findTargetInterface throws EffectNotFoundException`() {
        assertThrows(EffectNotFoundException::class.java) {
            proxyEffectStore.findTargetInterface(EffectImpl::class)
        }
    }

    @Test
    fun `test allTargetInterfaces`() {
        proxyEffectStore.registerTarget(EffectImpl::class, Effect::class)

        val allTargets = proxyEffectStore.allTargetInterfaces

        assertEquals(setOf(Effect::class), allTargets)
    }

    private interface Effect
    private class EffectImpl : Effect
    private class ProxyEffectImpl(
        val commandExecutor: CommandExecutor<Effect>,
    ) : Effect

}