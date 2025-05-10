package com.uandcode.effects.koin.internal

import androidx.lifecycle.ViewModel
import com.uandcode.effects.core.EffectProxyMarker
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.ResolutionContext
import org.koin.core.scope.Scope

class InstanceFactoryInterceptorTest {

    private lateinit var constructorArgStack: ConstructorArgStack

    @MockK
    private lateinit var beanDefinition: BeanDefinition<Effect>

    @MockK
    private lateinit var resolutionContext: ResolutionContext

    @MockK(relaxUnitFun = true)
    private lateinit var originFactory: InstanceFactory<Effect>

    private lateinit var factory: InstanceFactoryInterceptor<Effect>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        constructorArgStack = spyk(ConstructorArgStack())
        every { originFactory.beanDefinition } returns beanDefinition
        factory = InstanceFactoryInterceptor(constructorArgStack, originFactory)
    }

    @Test
    fun `get() with empty stack records and clears args`() {
        val expectedInstance = mockk<Effect>()
        every { originFactory.get(any()) } returns expectedInstance

        val instance = factory.get(resolutionContext)

        val slot = slot<ConstructorArg>()
        verifyOrder {
            constructorArgStack.push(capture(slot))
            originFactory.get(resolutionContext)
            constructorArgStack.clear()
        }
        assertSame(expectedInstance, instance)
    }

    @Test
    fun `second get() call is delegated to origin factory `() {
        val expectedInstance = mockk<Effect>()
        every { originFactory.get(any()) } returns expectedInstance

        val instance1 = factory.get(resolutionContext)
        val instance2 = factory.get(resolutionContext)

        val slot = slot<ConstructorArg>()
        verify(exactly = 1) {
            constructorArgStack.push(capture(slot))
            constructorArgStack.clear()
        }
        verify(exactly = 2) {
            originFactory.get(resolutionContext)
        }
        assertSame(expectedInstance, instance1)
        assertSame(expectedInstance, instance2)
    }

    @Test
    fun `get() with EffectProxyMarker assigns closeable`() {
        constructorArgStack.push(ConstructorArg())
        val expectedInstance = mockk<EffectProxyMarkerImpl>()
        every { originFactory.get(any()) } returns expectedInstance

        factory.get(resolutionContext)

        assertTrue(constructorArgStack.peek()?.hasCloseable() == true)
    }

    @Test
    fun `verify beanDefinition`() {
        assertSame(beanDefinition, factory.beanDefinition)
    }

    @Test
    fun `drop() is delegated to origin`() {
        val scope = mockk<Scope>()

        factory.drop(scope)

        verify(exactly = 1) {
            originFactory.drop(refEq(scope))
        }
    }

    @Test
    fun `dropAll() is delegated to origin`() {
        factory.dropAll()

        verify(exactly = 1) {
            originFactory.dropAll()
        }
    }

    @Test
    fun `isCreated() is delegated to origin`() {
        every { originFactory.isCreated(any()) } returns true andThen false

        val trueResult = factory.isCreated(resolutionContext)
        val falseResult = factory.isCreated(resolutionContext)

        assertTrue(trueResult)
        assertFalse(falseResult)
        verify(exactly = 2) {
            originFactory.isCreated(refEq(resolutionContext))
        }
    }

    private interface Effect
    abstract class EffectProxyMarkerImpl : Effect, EffectProxyMarker
    abstract class EffectViewModel : ViewModel(), Effect
}