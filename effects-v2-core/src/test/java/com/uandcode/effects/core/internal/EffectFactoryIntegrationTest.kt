package com.uandcode.effects.core.internal

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.stub.api.ProxyConfiguration
import com.uandcode.effects.stub.api.ProxyEffectStore
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EffectFactoryIntegrationTest {

    @MockK
    private lateinit var proxyEffectStore: ProxyEffectStore

    private lateinit var resourceStore: ObservableResourceStore<Effect>
    private lateinit var effectFactory: EffectFactory<Effect>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(ProxyEffectStoreProvider)
        resourceStore = ObservableResourceStoreImpl()
        effectFactory = EffectFactory(Effect::class, resourceStore)
        every { ProxyEffectStoreProvider.getGeneratedProxyEffectStore() } returns proxyEffectStore
        every { proxyEffectStore.proxyConfiguration } returns ProxyConfiguration()
    }

    @After
    fun tearDown() {
        unmockkObject(ProxyEffectStoreProvider)
    }

    @Test
    fun `provideProxy provides proxy implementation with resourceStore from constructor`() {
        every {
            proxyEffectStore.createProxy<Effect>(any(), any())
        } answers {
            ProxyEffectImpl(secondArg())
        }
        val effectImpl1 = EffectImpl()
        val effectImpl2 = EffectImpl()

        val proxyEffect = effectFactory.provideProxy()
        val controller = effectFactory.createController<EffectImpl>()
        val boundController = effectFactory.createBoundController { effectImpl2 }

        // assert effect implementation is not updated immediately
        proxyEffect.update()
        assertFalse(effectImpl1.isUpdated)
        // assert effect implementation is updated when controller attaches the implementation
        controller.start(effectImpl1)
        assertTrue(effectImpl1.isUpdated)
        // assert the second effect implementation is not updated (because it is not attached yet)
        assertFalse(effectImpl2.isUpdated)
        // assert the second effect implementation is not updated, because previous update()
        // call has been already processed
        boundController.start()
        assertFalse(effectImpl2.isUpdated)
        // assert the second effect implementation is updated, because it is the newest started implementation
        proxyEffect.update()
        assertTrue(effectImpl2.isUpdated)
    }

    private interface Effect {
        fun update()
    }

    private class EffectImpl : Effect {
        var isUpdated = false
        override fun update() {
            isUpdated = true
        }
    }

    private class ProxyEffectImpl(
        val commandExecutor: CommandExecutor<Effect>,
    ) : Effect {
        override fun update() {
            commandExecutor.execute { it.update() }
        }
    }

}