package com.uandcode.effects.core

import com.uandcode.effects.core.internal.components.EmptyEffectComponent
import com.uandcode.effects.core.internal.components.GlobalEffectComponent
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RootEffectComponentsTest {

    @MockK
    private lateinit var effectComponent: EffectComponent

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `test default empty component`() {
        assertEquals(EmptyEffectComponent, RootEffectComponents.empty)
    }

    @Test
    fun `test default global component`() {
        assertEquals(GlobalEffectComponent, RootEffectComponents.global)
    }

    @Test
    fun `test set custom empty component`() {
        RootEffectComponents.setEmpty(effectComponent)
        assertEquals(effectComponent, RootEffectComponents.empty)
    }

    @Test
    fun `test set custom global component`() {
        RootEffectComponents.setGlobal(effectComponent)
        assertEquals(effectComponent, RootEffectComponents.global)
    }

    @Test
    fun `test reset components`() {
        RootEffectComponents.setEmpty(effectComponent)
        RootEffectComponents.setGlobal(effectComponent)
        RootEffectComponents.resetComponents()
        assertEquals(EmptyEffectComponent, RootEffectComponents.empty)
        assertEquals(GlobalEffectComponent, RootEffectComponents.global)
    }

}
