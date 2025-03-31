package com.uandcode.effects.core.internal.components

import io.mockk.MockKAnnotations
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KClass

class AbstractEffectComponentTest {

    private lateinit var abstractEffectComponent: AbstractEffectComponent

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        abstractEffectComponent = makeAbstractEffectComponent()
    }

    @Test
    fun `test createChild`() {
        val childComponent = abstractEffectComponent.createChild(Any::class)
        assertTrue(childComponent is DefaultEffectComponent)
    }

    private fun makeAbstractEffectComponent() = object : AbstractEffectComponent() {
        override fun <T : Any> get(clazz: KClass<T>) = returnNothing()
        override fun <T : Any> getController(clazz: KClass<T>) = returnNothing()
        override fun <T : Any> getBoundController(clazz: KClass<T>, provider: () -> T) = returnNothing()
        override fun cleanUp() = Unit
    }

    private fun returnNothing(): Nothing {
        throw RuntimeException()
    }
}