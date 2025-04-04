package com.uandcode.effects.core.internal

import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.runtime.RuntimeProxyEffectFactory
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EffectClassManagerTest {

    @RelaxedMockK
    private lateinit var resourceStore: ObservableResourceStore<Effect>

    private lateinit var effectClassManager: EffectClassManager<Effect>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        effectClassManager = EffectClassManager(
            Effect::class,
            RuntimeProxyEffectFactory(),
            resourceStore,
        )
    }

    @Test
    fun `test provideProxy`() {
        val proxyEffect = effectClassManager.provideProxy()
        assertNotNull(proxyEffect)
    }

    @Test
    fun `test createController`() {
        val controller: EffectController<Effect> = effectClassManager.createController()

        assertTrue(controller is EffectControllerImpl)
    }

    @Test
    fun `test cleanUp`() {
        effectClassManager.cleanUp()

        verify(exactly = 1) { resourceStore.removeAllObservers() }
    }

    interface Effect

}