package com.uandcode.effects.core.lifecycle

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.core.RootEffectScopes
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.getProxy
import com.uandcode.effects.core.testing.lifecycle.TestLifecycleOwner
import com.uandcode.effects.core.testing.mocks.CombinedEffect
import com.uandcode.effects.core.testing.mocks.CombinedEffectWithTarget
import com.uandcode.effects.core.testing.mocks.Effect
import com.uandcode.effects.core.testing.mocks.Effect.Companion.EMIT_DELAY
import com.uandcode.effects.core.testing.mocks.Effect.Companion.expectedResult
import com.uandcode.effects.core.testing.mocks.Effect1
import com.uandcode.effects.core.testing.mocks.Effect2
import com.uandcode.effects.core.testing.mocks.EffectImpl
import com.uandcode.effects.core.testing.mocks.EffectWithDefaultMethod
import com.uandcode.effects.core.testing.mocks.EffectWithDefaultMethodImpl
import com.uandcode.effects.core.testing.mocks.EffectWithDefaultTarget
import com.uandcode.effects.core.testing.mocks.EffectWithDefaultTargetImpl
import com.uandcode.effects.core.testing.mocks.EffectWithExplicitHierarchy
import com.uandcode.effects.core.testing.mocks.EffectWithHierarchy
import com.uandcode.effects.core.testing.mocks.EffectWithOverriddenClose
import com.uandcode.effects.core.testing.mocks.EffectWithOverriddenCloseImpl
import com.uandcode.effects.core.testing.mocks.EffectWithTarget
import com.uandcode.effects.core.testing.mocks.EffectWithTargetImpl
import com.uandcode.effects.core.testing.mocks.NonTargetEffect3
import com.uandcode.effects.core.testing.mocks.SubEffect
import com.uandcode.effects.core.testing.mocks.SuperEffect
import com.uandcode.effects.core.testing.mocks.TargetEffect1
import com.uandcode.effects.core.testing.mocks.TargetEffect2
import com.uandcode.flowtest.CollectStatus
import com.uandcode.flowtest.JobStatus
import com.uandcode.flowtest.runFlowTest
import io.mockk.called
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CoreLifecycleIntegrationTest {

    private lateinit var scope: EffectScope

    @Before
    fun setUp() {
        scope = RootEffectScopes.empty.createChild(
            ManagedInterfaces.Everything,
        )
    }

    @Test
    fun `lazyEffect works for interface and implementation`() {
        val proxyEffect = getProxy<Effect>()
        val effectForInterface = spyk(EffectImpl())
        val effectForImplementation = spyk(EffectImpl())
        val lifecycleOwnerForInterface = TestLifecycleOwner()
        val lifecycleOwnerForImplementation = TestLifecycleOwner()
        lifecycleOwnerForInterface.lazyEffect<Effect>(scope) { effectForInterface }
        lifecycleOwnerForImplementation.lazyEffect<EffectImpl>(scope) { effectForImplementation }

        lifecycleOwnerForInterface.start()
        proxyEffect.unitRun("input1")
        verify(exactly = 1) {
            effectForInterface.unitRun("input1")
        }
        verify(exactly = 0) {
            effectForImplementation.unitRun(any())
        }

        lifecycleOwnerForInterface.stop()
        clearMocks(effectForInterface)
        lifecycleOwnerForImplementation.start()
        proxyEffect.unitRun("input2")
        verify(exactly = 0) {
            effectForInterface.unitRun(any())
        }
        verify(exactly = 1) {
            effectForImplementation.unitRun("input2")
        }
    }

    @Test
    fun `lazyEffect with target arg in annotation works with interface and implementation`() {
        val proxyEffect = getProxy<EffectWithTarget>()
        val effectForInterface = spyk(EffectWithTargetImpl())
        val effectForImplementation = spyk(EffectWithTargetImpl())
        val lifecycleOwnerForInterface = TestLifecycleOwner()
        val lifecycleOwnerForImplementation = TestLifecycleOwner()
        lifecycleOwnerForInterface.lazyEffect<EffectWithTarget>(scope) { effectForInterface }
        lifecycleOwnerForImplementation.lazyEffect<EffectWithTargetImpl>(scope) { effectForImplementation }

        lifecycleOwnerForInterface.start()
        proxyEffect.run("input1")
        verify(exactly = 1) {
            effectForInterface.run("input1")
        }
        verify(exactly = 0) {
            effectForImplementation.run(any())
        }

        lifecycleOwnerForInterface.stop()
        clearMocks(effectForInterface)
        lifecycleOwnerForImplementation.start()
        proxyEffect.run("input2")
        verify(exactly = 0) {
            effectForInterface.run(any())
        }
        verify(exactly = 1) {
            effectForImplementation.run("input2")
        }
    }

    @Test
    fun `lazyEffect with default effect target arg works for interface and implementation`() {
        val proxyEffect = getProxy<EffectWithDefaultTarget>()
        val effectForInterface = spyk(EffectWithDefaultTargetImpl())
        val effectForImplementation = spyk(EffectWithDefaultTargetImpl())
        val lifecycleOwnerForInterface = TestLifecycleOwner()
        val lifecycleOwnerForImplementation = TestLifecycleOwner()
        lifecycleOwnerForInterface.lazyEffect<EffectWithDefaultTarget>(scope) { effectForInterface }
        lifecycleOwnerForImplementation.lazyEffect<EffectWithDefaultTargetImpl>(scope) { effectForImplementation }

        lifecycleOwnerForInterface.start()
        proxyEffect.run("input1")
        verify(exactly = 1) {
            effectForInterface.run("input1")
        }
        verify(exactly = 0) {
            effectForImplementation.run(any())
        }

        lifecycleOwnerForInterface.stop()
        clearMocks(effectForInterface)
        lifecycleOwnerForImplementation.start()
        proxyEffect.run("input2")
        verify(exactly = 0) {
            effectForInterface.run(any())
        }
        verify(exactly = 1) {
            effectForImplementation.run("input2")
        }
    }

    @Test
    fun `proxy with attached implementation delivers call immediately`() {
        val proxyEffect = getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<EffectImpl>(scope) { effectImpl }
        lifecycleOwner.start()

        proxyEffect.unitRun("input")

        verify(exactly = 1) {
            effectImpl.unitRun("input")
        }
    }

    @Test
    fun `proxy with detached implementation delivers call after attaching`() {
        val proxyEffect = getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<EffectImpl>(scope) { effectImpl }

        proxyEffect.unitRun("input")
        verify(exactly = 0) {
            effectImpl.unitRun(any())
        }
        lifecycleOwner.start()
        verify(exactly = 1) {
            effectImpl.unitRun("input")
        }
    }

    @Test
    fun `proxy delivers call to last attached implementation`() {
        val proxyEffect = getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val lifecycleOwner1 = TestLifecycleOwner()
        val lifecycleOwner2 = TestLifecycleOwner()
        lifecycleOwner1.lazyEffect<EffectImpl>(scope) { effectImpl1 }
        lifecycleOwner2.lazyEffect<EffectImpl>(scope) { effectImpl2 }
        lifecycleOwner1.start()
        lifecycleOwner2.start()

        proxyEffect.unitRun("input")

        verify(exactly = 0) {
            effectImpl1.unitRun(any())
        }
        verify(exactly = 1) {
            effectImpl2.unitRun("input")
        }
    }

    @Test
    fun `controller does not attach effect is it has already attached effect`() {
        val proxyEffect = getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<EffectImpl>(scope) { effectImpl1 }
        lifecycleOwner.start()
        lifecycleOwner.start()

        proxyEffect.unitRun("input")

        verify(exactly = 1) {
            effectImpl1.unitRun(any())
        }
    }

    @Test
    fun `proxy delivers one call to one implementation`() {
        val proxyEffect = getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val lifecycleOwner1 = TestLifecycleOwner()
        val lifecycleOwner2 = TestLifecycleOwner()
        lifecycleOwner1.lazyEffect<EffectImpl>(scope) { effectImpl1 }
        lifecycleOwner2.lazyEffect<EffectImpl>(scope) { effectImpl2 }

        proxyEffect.unitRun("input")
        lifecycleOwner1.start()
        lifecycleOwner2.start()

        verify(exactly = 1) {
            effectImpl1.unitRun("input")
        }
        verify(exactly = 0) {
            effectImpl2.unitRun(any())
        }
    }

    @Test
    fun `proxy does not deliver call to detached implementation`() {
        val proxyEffect = getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<EffectImpl>(scope) { effectImpl }
        lifecycleOwner.start()
        lifecycleOwner.stop()

        proxyEffect.unitRun("input")

        verify(exactly = 0) {
            effectImpl.unitRun(any())
        }
    }

    @Test
    fun `proxy delivers call after re-attaching implementation`() {
        val proxyEffect = getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<EffectImpl>(scope) { effectImpl }
        lifecycleOwner.start()
        lifecycleOwner.stop()

        proxyEffect.unitRun("input")
        lifecycleOwner.start()

        verify(exactly = 1) {
            effectImpl.unitRun("input")
        }
    }

    @Test
    fun `proxy ignores exception from unit call`() = runTest {
        val proxyEffect = getProxy<Effect>()
        val effectImpl = mockk<Effect>()
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<Effect>(scope) { effectImpl }
        coEvery { effectImpl.unitRun(any()) } throws IllegalStateException()
        lifecycleOwner.start()

        val result = runCatching {
            proxyEffect.unitRun("input")
        }

        assertTrue(result.isSuccess)
    }

    @Test
    fun `proxy returns result from suspend call`() = runTest {
        val proxyEffect = getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<EffectImpl>(scope) { effectImpl }
        lifecycleOwner.start()

        val result = proxyEffect.coroutineRun("input")

        assertEquals(expectedResult("input"), result)
    }

    @Test
    fun `proxy waits for result from suspend call`() = runFlowTest {
        val proxyEffect = getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<EffectImpl>(this@CoreLifecycleIntegrationTest.scope) { effectImpl }

        val result = executeInBackground {
            proxyEffect.coroutineRun("input")
        }

        // assert the job is still running after emit delay because
        // the effect implementation should not be called
        advanceTimeBy(EMIT_DELAY + 1)
        assertEquals(JobStatus.Executing, result.status)

        // attach the implementation, but the job is still running
        // before result is emitted
        lifecycleOwner.start()
        advanceTimeBy(EMIT_DELAY)
        assertEquals(JobStatus.Executing, result.status)

        // after the emit delay -> job done
        advanceTimeBy(1)
        assertEquals(JobStatus.Completed(expectedResult("input")), result.status)
    }

    @Test
    fun `proxy re-executes suspend call after re-attaching`() = runFlowTest {
        val proxyEffect = getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<EffectImpl>(this@CoreLifecycleIntegrationTest.scope) { effectImpl }
        lifecycleOwner.start()

        val result = executeInBackground {
            proxyEffect.coroutineRun("input")
        }

        // assert job is still running after detaching the implementation
        advanceTimeBy(EMIT_DELAY)
        lifecycleOwner.stop()
        advanceTimeBy(1)
        assertEquals(JobStatus.Executing, result.status)
        // re-attach the implementation, the job must be restarted from the beginning
        lifecycleOwner.start()
        advanceTimeBy(EMIT_DELAY)
        assertEquals(JobStatus.Executing, result.status) // still running due to re-attachment
        advanceTimeBy(1)
        assertEquals(JobStatus.Completed(expectedResult("input")), result.status)
        coVerify(exactly = 2) {
            effectImpl.coroutineRun("input")
        }
    }

    @Test
    fun `proxy switches implementation for suspend call after detaching`() = runFlowTest {
        val byFirstEffect = "byFirstEffect"
        val proxyEffect = getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl(byFirstEffect))
        val effectImpl2 = spyk(EffectImpl())
        val lifecycleOwner1 = TestLifecycleOwner()
        val lifecycleOwner2 = TestLifecycleOwner()
        lifecycleOwner1.lazyEffect<EffectImpl>(this@CoreLifecycleIntegrationTest.scope) { effectImpl1 }
        lifecycleOwner2.lazyEffect<EffectImpl>(this@CoreLifecycleIntegrationTest.scope) { effectImpl2 }
        lifecycleOwner1.start()
        lifecycleOwner2.start()

        val result = executeInBackground {
            proxyEffect.coroutineRun("input")
        }

        // job must be started on the second effect implementation
        coVerify(exactly = 0) {
            effectImpl1.coroutineRun(any())
        }
        coVerify(exactly = 1) {
            effectImpl2.coroutineRun("input")
        }
        assertEquals(JobStatus.Executing, result.status)
        // assert job is re-executed immediately on the first effect implementation
        // after detaching the second one
        advanceTimeBy(EMIT_DELAY)
        lifecycleOwner2.stop()
        coVerify(exactly = 1) {
            effectImpl1.coroutineRun("input")
        }
        assertEquals(JobStatus.Executing, result.status)
        // assert job is not done after emitDelay due to switching to other implementation
        advanceTimeBy(EMIT_DELAY)
        assertEquals(JobStatus.Executing, result.status)
        // assert job done
        advanceTimeBy(1)
        assertEquals(JobStatus.Completed(expectedResult("input", byFirstEffect)), result.status)
    }

    @Test
    fun `proxy delivers exception from suspend call`() = runTest {
        val proxyEffect = getProxy<Effect>()
        val effectImpl = mockk<Effect>()
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<Effect>(scope) { effectImpl }
        coEvery { effectImpl.coroutineRun(any()) } throws IllegalStateException()
        lifecycleOwner.start()

        val exception = runCatching {
            proxyEffect.coroutineRun("input")
        }.exceptionOrNull()

        assertTrue(exception is IllegalStateException)
    }

    @Test
    fun `proxy executes flow on all effects`() = runFlowTest {
        val byFirstEffect = "byFirstEffect"
        val bySecondEffect = "byFirstEffect"
        val proxyEffect = getProxy<Effect>()
        val effectImpl1 = EffectImpl(byFirstEffect)
        val effectImpl2 = EffectImpl(bySecondEffect)
        val lifecycleOwner1 = TestLifecycleOwner()
        val lifecycleOwner2 = TestLifecycleOwner()
        lifecycleOwner1.lazyEffect<EffectImpl>(this@CoreLifecycleIntegrationTest.scope) { effectImpl1 }
        lifecycleOwner2.lazyEffect<EffectImpl>(this@CoreLifecycleIntegrationTest.scope) { effectImpl2 }
        lifecycleOwner1.start()
        lifecycleOwner2.start()

        val state = proxyEffect.flowRun().startCollecting()

        // assert items from both effects are delivered
        effectImpl1.send("1")
        assertEquals(expectedResult("1", byFirstEffect), state.lastItem)
        effectImpl2.send("2")
        assertEquals(expectedResult("2", bySecondEffect), state.lastItem)
        // assert items are not delivered from detached effects
        lifecycleOwner1.stop()
        effectImpl1.send("3")
        assertEquals(expectedResult("2", bySecondEffect), state.lastItem)
        // assert items are delivered again after attaching
        lifecycleOwner1.start()
        effectImpl1.send("4")
        assertEquals(expectedResult("4", byFirstEffect), state.lastItem)
    }

    @Test
    fun `proxy delivers exception from flow call and cancels listening`() = runFlowTest {
        val proxyEffect = getProxy<Effect>()
        val effectImpl1 = EffectImpl()
        val effectImpl2 = EffectImpl()
        val lifecycleOwner1 = TestLifecycleOwner()
        val lifecycleOwner2 = TestLifecycleOwner()
        lifecycleOwner1.lazyEffect<Effect>(this@CoreLifecycleIntegrationTest.scope) { effectImpl1 }
        lifecycleOwner2.lazyEffect<Effect>(this@CoreLifecycleIntegrationTest.scope) { effectImpl2 }
        lifecycleOwner1.start()
        lifecycleOwner2.start()

        val state = proxyEffect.flowRun().startCollecting()
        effectImpl1.fail(IllegalStateException())

        val exception = (state.collectStatus as CollectStatus.Failed).exception
        assertTrue(exception is IllegalStateException)
    }

    @Test
    fun `proxy completes result flow when all flows from all effects complete`() = runFlowTest {
        val proxyEffect = getProxy<Effect>()
        val effectImpl1 = EffectImpl()
        val effectImpl2 = EffectImpl()
        val lifecycleOwner1 = TestLifecycleOwner()
        val lifecycleOwner2 = TestLifecycleOwner()
        lifecycleOwner1.lazyEffect<Effect>(this@CoreLifecycleIntegrationTest.scope) { effectImpl1 }
        lifecycleOwner2.lazyEffect<Effect>(this@CoreLifecycleIntegrationTest.scope) { effectImpl2 }
        lifecycleOwner1.start()
        lifecycleOwner2.start()

        val state = proxyEffect.flowRun().startCollecting()

        effectImpl1.complete()
        assertEquals(CollectStatus.Collecting, state.collectStatus)
        effectImpl2.complete()
        assertEquals(CollectStatus.Completed, state.collectStatus)
    }

    @Test
    fun `proxy does not complete result flow if all effects are detached`() = runFlowTest {
        val proxyEffect = getProxy<Effect>()
        val effectImpl1 = EffectImpl()
        val effectImpl2 = EffectImpl()
        val lifecycleOwner1 = TestLifecycleOwner()
        val lifecycleOwner2 = TestLifecycleOwner()
        lifecycleOwner1.lazyEffect<Effect>(this@CoreLifecycleIntegrationTest.scope) { effectImpl1 }
        lifecycleOwner2.lazyEffect<Effect>(this@CoreLifecycleIntegrationTest.scope) { effectImpl2 }
        lifecycleOwner1.start()
        lifecycleOwner2.start()

        val state = proxyEffect.flowRun().startCollecting()

        // detach the first effect -> still collecting
        lifecycleOwner1.stop()
        assertEquals(CollectStatus.Collecting, state.collectStatus)
        // detach the second effect -> continue collecting anyway
        lifecycleOwner2.stop()
        assertEquals(CollectStatus.Collecting, state.collectStatus)
        // attach the first effect again -> items must be delivered
        lifecycleOwner1.start()
        effectImpl1.send("input")
        assertEquals(expectedResult("input"), state.lastItem)
        // complete the first effect -> collecting finished, because
        // only 1 effect is attached
        effectImpl1.complete()
        assertEquals(CollectStatus.Completed, state.collectStatus)
    }

    @Test
    fun `two suspend calls should not interfere`() = runFlowTest {
        val proxyEffect1 = getProxy<Effect>()
        val proxyEffect2 = getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<Effect>(this@CoreLifecycleIntegrationTest.scope) { effectImpl }
        lifecycleOwner.start()

        val result1 = executeInBackground {
            proxyEffect1.coroutineRun("input1", emitDelay = 100)
        }
        advanceTimeBy(10)
        val result2 = executeInBackground {
            proxyEffect2.coroutineRun("input2", emitDelay = 50)
        }

        advanceTimeBy(90)
        assertEquals(JobStatus.Executing, result1.status)
        assertEquals(JobStatus.Completed(expectedResult("input2")), result2.status)
        advanceTimeBy(1)
        assertEquals(JobStatus.Completed(expectedResult("input1")), result1.status)
    }

    @Test
    fun `proxy implements AutoCloseable which cancels unit commands`() = runFlowTest {
        val proxy = getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<Effect>(this@CoreLifecycleIntegrationTest.scope) { effectImpl }

        proxy.unitRun("input")
        (proxy as AutoCloseable).close()
        lifecycleOwner.start()

        verify(exactly = 0) {
            effectImpl.unitRun(any())
        }
    }

    @Test
    fun `overridden close() call is not delivered to effect implementation`() {
        val proxy = getProxy<EffectWithOverriddenClose>()
        val effectImpl = spyk(EffectWithOverriddenCloseImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect<EffectWithOverriddenClose>(scope) { effectImpl }

        proxy.run("input")
        proxy.close()
        lifecycleOwner.start()

        verify(exactly = 0) {
            effectImpl.run(any())
            effectImpl.close()
        }
    }

    @Test
    fun `lazyEffect of combined effect can work with any interface`() {
        val proxy1 = scope.getProxy<Effect1>()
        val proxy2 = scope.getProxy<Effect2>()
        val combinedEffect = spyk(CombinedEffect())
        val lifecycleOwner = TestLifecycleOwner()
        val effect1 by lifecycleOwner.lazyEffect<Effect1>(scope) { combinedEffect }
        val effect2 by lifecycleOwner.lazyEffect<Effect2>(scope) { combinedEffect }
        lifecycleOwner.start()

        proxy1.run1("input")
        proxy2.run2("input")

        verify(exactly = 1) {
            combinedEffect.run1("input")
            combinedEffect.run2("input")
        }
        assertSame(combinedEffect, effect1)
        assertSame(combinedEffect, effect2)
    }

    @Test
    fun `lazyEffect of interface of combined effect connects only to own interface`() {
        val proxy1 = scope.getProxy<Effect1>()
        val combinedEffect = spyk(CombinedEffect())
        val lifecycleOwner1 = TestLifecycleOwner()
        val lifecycleOwner2 = TestLifecycleOwner()
        lifecycleOwner1.lazyEffect<Effect1>(scope) { combinedEffect }
        lifecycleOwner2.lazyEffect<Effect2>(scope) { combinedEffect }

        lifecycleOwner2.start()
        proxy1.run1("input")
        verify(exactly = 0) {
            combinedEffect.run1(any())
        }

        lifecycleOwner1.start()
        verify(exactly = 1) {
            combinedEffect.run1("input")
        }
    }

    @Test
    fun `lazyEffect of combined effect connects to all target interfaces`() {
        val proxy1 = scope.getProxy<Effect1>()
        val proxy2 = scope.getProxy<Effect2>()
        val combinedEffect = spyk(CombinedEffect())
        val lifecycle = TestLifecycleOwner()
        lifecycle.lazyEffect<CombinedEffect>(scope) { combinedEffect }

        lifecycle.start()
        proxy1.run1("input")
        proxy2.run2("input")

        verify(exactly = 1) {
            combinedEffect.run1("input")
            combinedEffect.run2("input")
        }
    }

    @Test
    fun `lazyEffect of combined effect disconnects from all target interfaces`() {
        val proxy1 = scope.getProxy<Effect1>()
        val proxy2 = scope.getProxy<Effect2>()
        val combinedEffect = spyk(CombinedEffect())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect { CombinedEffect() }

        lifecycleOwner.start()
        lifecycleOwner.stop()
        proxy1.run1("input")
        proxy2.run2("input")

        verify(exactly = 0) {
            combinedEffect.run1(any())
            combinedEffect.run2(any())
        }
    }

    @Test
    fun `lazyEffect of combined effect gathers interfaces from scope hierarchy`() {
        val childScope = RootEffectScopes.empty
            .createChild(
                ManagedInterfaces.ListOf(Effect1::class),
            )
            .createChild(
                ManagedInterfaces.ListOf(Effect2::class),
            )
        val proxy1 = childScope.getProxy(Effect1::class)
        val proxy2 = childScope.getProxy(Effect2::class)
        val combinedEffect = spyk(CombinedEffect())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect(childScope) { combinedEffect }
        lifecycleOwner.start()

        proxy1.run1("input")
        proxy2.run2("input")

        verify(exactly = 1) {
            combinedEffect.run1("input")
            combinedEffect.run2("input")
        }
    }

    @Test
    fun `lazyEffect of combined effect with missing at least 1 interface fails`() {
        val childScope = RootEffectScopes.empty
            .createChild(
                ManagedInterfaces.ListOf(Effect1::class),
            )
        childScope.getProxy(Effect1::class)
        val lifecycleOwner = TestLifecycleOwner()

        val result = runCatching {
            lifecycleOwner.lazyEffect(childScope) { CombinedEffect() }
        }

        assertTrue(result.exceptionOrNull() is EffectNotFoundException)
    }

    @Test
    fun `lazyEffect for combined effect with target arg excludes non-listed interfaces`() {
        val proxy1 = scope.getProxy<TargetEffect1>()
        val proxy2 = scope.getProxy<TargetEffect2>()
        val combinedEffect = spyk(CombinedEffectWithTarget())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect(scope) { combinedEffect }
        lifecycleOwner.start()

        proxy1.run1("input")
        proxy2.run2("input")

        verify(exactly = 1) {
            combinedEffect.run1("input")
            combinedEffect.run2("input")
        }
        verify(exactly = 0) {
            combinedEffect.run3(any())
        }
        val result = runCatching { scope.getProxy<NonTargetEffect3>() }
        assertTrue(result.exceptionOrNull() is EffectNotFoundException)
    }

    @Test
    fun `default method of effect should not be overridden`() {
        val proxy = scope.getProxy<EffectWithDefaultMethod>()
        val effect = spyk(EffectWithDefaultMethodImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect(scope) { effect }

        proxy.defaultRun("input")

        verify {
            effect wasNot called
        }
        lifecycleOwner.start()
        verify(exactly = 1) {
            effect.run("default input")
        }
        verify(exactly = 0) {
            effect.defaultRun(any())
        }
    }

    @Test
    fun `nested default methods of effect should not be overridden`() {
        val proxy = scope.getProxy<EffectWithDefaultMethod>()
        val effect = spyk(EffectWithDefaultMethodImpl())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect(scope) { effect }

        proxy.combinedDefaultRun("input")

        verify {
            effect wasNot called
        }
        lifecycleOwner.start()
        verify(exactly = 1) {
            effect.run("default combined input")
        }
        verify(exactly = 0) {
            effect.defaultRun(any())
            effect.combinedDefaultRun(any())
        }
    }

    @Test
    fun `proxy created from sub-effect delivers call on super-interface`() {
        val proxy = scope.getProxy<SubEffect>()
        val effect = spyk(EffectWithHierarchy())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect(scope) { effect }
        lifecycleOwner.start()

        proxy.superRun("input")

        verify(exactly = 1) {
            effect.superRun("input")
        }
    }

    @Test
    fun `proxy created from super-effect does not deliver call on super-interface`() {
        val proxy = scope.getProxy<SuperEffect>()
        val effect = spyk(EffectWithHierarchy())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect(scope) { effect }
        lifecycleOwner.start()

        proxy.superRun("input")

        verify(exactly = 0) {
            effect.superRun("input")
        }
    }

    @Test
    fun `proxy created from explicit super-effect delivers call on super-interface`() {
        val proxy = scope.getProxy<SuperEffect>()
        val effect = spyk(EffectWithExplicitHierarchy())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect(scope) { effect }
        lifecycleOwner.start()

        proxy.superRun("input")

        verify(exactly = 1) {
            effect.superRun("input")
        }
    }

    @Test
    fun `proxy created from sub-effect delivers default call on super-interface`() {
        val proxy = scope.getProxy<SubEffect>()
        val effect = spyk(EffectWithHierarchy())
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.lazyEffect(scope) { effect }
        lifecycleOwner.start()

        proxy.defaultRun("input")

        verify(exactly = 0) {
            effect.defaultRun(any())
        }
        verify(exactly = 1) {
            effect.superRun("combined input")
        }
    }

    private inline fun <reified T : Any> getProxy(): T {
        return scope.getProxy()
    }

}
