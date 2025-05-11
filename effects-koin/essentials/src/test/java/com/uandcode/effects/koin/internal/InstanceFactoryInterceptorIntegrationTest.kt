package com.uandcode.effects.koin.internal

import androidx.lifecycle.ViewModel
import com.uandcode.effects.core.EffectProxyMarker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class InstanceFactoryInterceptorIntegrationTest {

    @Test
    fun `verify ViewModel closes effects`() {
        val stack = ConstructorArgStack()
        val module = module {
            factoryOf(::EffectViewModel)
            factoryOf(::EffectProxyMarkerImpl)
        }
        val koin = koinApplication {
            modules(module)
            overrideInstances(stack)
        }.koin

        val viewModel = koin.get<EffectViewModel>()

        assertFalse(viewModel.effect.isClosed)
        viewModel.getCloseable<AutoCloseable>("com.uandcode.effects.koin.closeable")?.close()
        assertTrue(viewModel.effect.isClosed)
        assertEquals(0, stack.count())
    }

    @Test
    fun `verify ViewModel with indirect instantiation closes effects`() {
        val stack = ConstructorArgStack()
        val module = module {
            factoryOf(::EffectViewModel)
            factoryOf(::EffectProxyMarkerImpl)
            factoryOf(::ViewModelBox)
        }
        val koin = koinApplication {
            modules(module)
            overrideInstances(stack)
        }.koin

        val viewModelBox = koin.get<ViewModelBox>()
        val viewModel = viewModelBox.viewModel

        assertFalse(viewModel.effect.isClosed)
        viewModel.getCloseable<AutoCloseable>("com.uandcode.effects.koin.closeable")?.close()
        assertTrue(viewModel.effect.isClosed)
        assertEquals(0, stack.count())
    }

    @Test
    fun `verify ViewModel closes effects from other classes`() {
        val stack = ConstructorArgStack()
        val module = module {
            factoryOf(::IndirectEffectViewModel)
            factoryOf(::EffectProxyMarkerImpl)
            factoryOf(::EffectBox)
        }
        val koin = koinApplication {
            modules(module)
            overrideInstances(stack)
        }.koin

        val viewModel = koin.get<IndirectEffectViewModel>()
        val effect = viewModel.effectBox.effect

        assertFalse(effect.isClosed)
        viewModel.getCloseable<AutoCloseable>("com.uandcode.effects.koin.closeable")?.close()
        assertTrue(effect.isClosed)
        assertEquals(0, stack.count())
    }

    private interface Effect

    private class EffectProxyMarkerImpl : Effect, EffectProxyMarker {
        var isClosed = false
        override fun close() {
            isClosed = true
        }
    }

    private class EffectViewModel(
        val effect: EffectProxyMarkerImpl,
    ) : ViewModel(), Effect

    private class ViewModelBox(
        val viewModel: EffectViewModel
    )

    private class EffectBox(
        val effect: EffectProxyMarkerImpl
    )

    private class IndirectEffectViewModel(
        val effectBox: EffectBox,
    ) : ViewModel(), Effect

}