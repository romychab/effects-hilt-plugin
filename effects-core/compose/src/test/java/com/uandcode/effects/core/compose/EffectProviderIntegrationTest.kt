@file:OptIn(ExperimentalCoroutinesApi::class)

package com.uandcode.effects.core.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.core.RootEffectScopes
import com.uandcode.effects.core.compose.exceptions.EffectProviderNotFoundException
import com.uandcode.effects.core.compose.testing.TestComposition
import com.uandcode.effects.core.compose.testing.TestContainer
import com.uandcode.effects.core.compose.testing.TestElement
import com.uandcode.effects.core.compose.testing.setContent
import com.uandcode.effects.core.factories.DefaultProxyEffectFactory
import com.uandcode.effects.core.mocks.Effect
import com.uandcode.effects.core.mocks.EffectImpl
import com.uandcode.effects.core.mocks.SimpleEffect1
import com.uandcode.effects.core.mocks.SimpleEffect1Impl
import com.uandcode.effects.core.mocks.SimpleEffect2
import com.uandcode.effects.core.mocks.SimpleEffect2Impl
import com.uandcode.flowtest.runFlowTest
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class EffectProviderIntegrationTest {

    private lateinit var effectScope: EffectScope

    private lateinit var testScope: TestScope

    @Before
    fun setUp() {
        effectScope = RootEffectScopes.empty.createChild(
            managedInterfaces = ManagedInterfaces.Everything,
            proxyEffectFactory = DefaultProxyEffectFactory(),
        )
        testScope = TestScope()
        Dispatchers.setMain(UnconfinedTestDispatcher(testScope.testScheduler))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getEffect returns effect instance provided by EffectProvider`() = runComposeTest {
        val expectedEffect = remember { EffectImpl() }
        EffectProvider(expectedEffect, scope = effectScope) {
            val result = getEffect<EffectImpl>()
            assertSame(expectedEffect, result)
        }
    }

    @Test
    fun `getEffect can return effect interface`() = runComposeTest {
        val expectedEffect = remember { EffectImpl() }
        EffectProvider(expectedEffect, scope = effectScope) {
            val result = getEffect<Effect>()
            assertSame(expectedEffect, result)
        }
    }

    @Test
    fun `getEffect can return effect instances from any nested EffectProvider`() = runComposeTest {
        val expectedEffect1 = remember { SimpleEffect1Impl() }
        EffectProvider(expectedEffect1, scope = effectScope) {
            TestElement("name1")
            TestContainer("name2") {
                val expectedEffect2 = remember { SimpleEffect2Impl() }
                EffectProvider(expectedEffect2) {
                    val resultEffect1 = getEffect<SimpleEffect1Impl>()
                    val resultEffect2 = getEffect<SimpleEffect2Impl>()

                    assertSame(expectedEffect1, resultEffect1)
                    assertSame(expectedEffect2, resultEffect2)
                }
            }
        }
    }

    @Test
    fun `getEffect can return effect interfaces from any nested EffectProvider`() = runComposeTest {
        val expectedEffect1 = remember { SimpleEffect1Impl() }
        EffectProvider(expectedEffect1, scope = effectScope) {
            TestElement("name1")
            TestContainer("name2") {
                val expectedEffect2 = remember { SimpleEffect2Impl() }
                EffectProvider(expectedEffect2) {
                    val resultEffect1 = getEffect<SimpleEffect1>()
                    val resultEffect2 = getEffect<SimpleEffect2>()

                    assertSame(expectedEffect1, resultEffect1)
                    assertSame(expectedEffect2, resultEffect2)
                }
            }
        }
    }

    @Test
    fun `EffectProvider connects effect to proxy after onStart`() = testScope.runFlowTest {
        val proxy = effectScope.getProxy(Effect::class)
        val effect = spyk(EffectImpl())
        val composition = setContent {
            EffectProvider(effect, scope = effectScope) {
            }
        }

        composition.updateLifecycle { start() }
        proxy.unitRun("input")

        verify(exactly = 1) {
            effect.unitRun("input")
        }
    }

    @Test
    fun `EffectProvider does not connect effect to proxy after onCreate`() = testScope.runFlowTest {
        val proxy = effectScope.getProxy(Effect::class)
        val effect = spyk(EffectImpl())
        val composition = setContent {
            EffectProvider(effect, scope = effectScope) {
            }
        }

        composition.updateLifecycle { create() }
        proxy.unitRun("input")

        verify(exactly = 0) {
            effect.unitRun(any())
        }
    }

    @Test
    fun `EffectProvider disconnects effect from proxy after onStop`() = testScope.runFlowTest {
        val proxy = effectScope.getProxy(Effect::class)
        val effect = spyk(EffectImpl())
        val composition = setContent {
            EffectProvider(effect, scope = effectScope) {
            }
        }

        composition.updateLifecycle { start() }
        composition.updateLifecycle { stop() }
        proxy.unitRun("input")

        verify(exactly = 0) {
            effect.unitRun(any())
        }
    }

    @Test
    fun `EffectProvider reconnects effect to proxy after onStop and onStart`() = testScope.runFlowTest {
        val proxy = effectScope.getProxy(Effect::class)
        val effect = spyk(EffectImpl())
        val composition = setContent {
            EffectProvider(effect, scope = effectScope) {
            }
        }

        composition.updateLifecycle { start() }
        composition.updateLifecycle { stop() }
        composition.updateLifecycle { start() }
        proxy.unitRun("input")

        verify(exactly = 1) {
            effect.unitRun("input")
        }
    }

    @Test
    fun `EffectProvider disconnects from proxy when composition is destroyed`() = testScope.runFlowTest {
        var state by mutableStateOf(true)
        val proxy = effectScope.getProxy(Effect::class)
        val effect = spyk(EffectImpl())
        val composition = setContent {
            if (state) {
                TestContainer("") {
                    EffectProvider(effect, scope = effectScope) {
                    }
                }
            }
        }
        composition.updateLifecycle { start() }
        state = false
        runCurrent()
        advanceTimeBy(1000)
        composition.runRecomposition()
        runCurrent()
        advanceTimeBy(1000)
        state = false
        runCurrent()
        advanceTimeBy(1000)

        proxy.unitRun("input")

        verify(exactly = 0) {
            effect.unitRun(any())
        }
    }

    private fun runComposeTest(content: @Composable () -> Unit) {
        testScope.runFlowTest {
            setContent(content)
        }
    }

}
