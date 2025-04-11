package com.uandcode.effects.core.runtime

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.core.RootEffectScopes
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.getController
import com.uandcode.effects.core.getProxy
import com.uandcode.effects.core.mocks.CombinedEffect
import com.uandcode.effects.core.mocks.CombinedEffectWithTarget
import com.uandcode.effects.core.mocks.Effect
import com.uandcode.effects.core.mocks.Effect.Companion.EMIT_DELAY
import com.uandcode.effects.core.mocks.Effect.Companion.expectedResult
import com.uandcode.effects.core.mocks.Effect1
import com.uandcode.effects.core.mocks.Effect2
import com.uandcode.effects.core.mocks.EffectImpl
import com.uandcode.effects.core.mocks.EffectWithDefaultTarget
import com.uandcode.effects.core.mocks.EffectWithDefaultTargetImpl
import com.uandcode.effects.core.mocks.EffectWithNonOverriddenClose
import com.uandcode.effects.core.mocks.EffectWithNonOverriddenCloseImpl
import com.uandcode.effects.core.mocks.EffectWithTarget
import com.uandcode.effects.core.mocks.EffectWithTargetImpl
import com.uandcode.effects.core.mocks.NonTargetEffect3
import com.uandcode.effects.core.mocks.TargetEffect1
import com.uandcode.effects.core.mocks.TargetEffect2
import com.uandcode.flowtest.CollectStatus
import com.uandcode.flowtest.JobStatus
import com.uandcode.flowtest.runFlowTest
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CoreRuntimeIntegrationTest {

    private lateinit var scope: EffectScope

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        RootEffectScopes.setGlobal(RuntimeEffectScopes.create())
        scope = RootEffectScopes.global
    }

    @After
    fun tearDown() {
        RootEffectScopes.resetGlobalScope()
    }

    @Test
    fun `getController() returns controllers for interface and implementation`() {
        val proxyEffect = scope.getProxy<Effect>()
        val interfaceController = scope.getController<Effect>()
        val implController = scope.getController<EffectImpl>()
        val effectForInterface = spyk(EffectImpl())
        val effectForImplementation = spyk(EffectImpl())

        interfaceController.start(effectForInterface)
        proxyEffect.unitRun("input1")
        verify(exactly = 1) {
            effectForInterface.unitRun("input1")
        }
        verify(exactly = 0) {
            effectForImplementation.unitRun(any())
        }

        interfaceController.stop()
        clearMocks(effectForInterface)
        implController.start(effectForImplementation)
        proxyEffect.unitRun("input2")
        verify(exactly = 0) {
            effectForInterface.unitRun(any())
        }
        verify(exactly = 1) {
            effectForImplementation.unitRun("input2")
        }
    }

    @Test
    fun `getController() with target arg in annotation returns controllers`() {
        val proxyEffect = scope.getProxy<EffectWithTarget>()
        val interfaceController = scope.getController<EffectWithTarget>()
        val implController = scope.getController<EffectWithTargetImpl>()
        val effectForInterface = spyk(EffectWithTargetImpl())
        val effectForImplementation = spyk(EffectWithTargetImpl())

        interfaceController.start(effectForInterface)
        proxyEffect.run("input1")
        verify(exactly = 1) {
            effectForInterface.run("input1")
        }
        verify(exactly = 0) {
            effectForImplementation.run(any())
        }

        interfaceController.stop()
        clearMocks(effectForInterface)
        implController.start(effectForImplementation)
        proxyEffect.run("input2")
        verify(exactly = 0) {
            effectForInterface.run(any())
        }
        verify(exactly = 1) {
            effectForImplementation.run("input2")
        }
    }

    @Test
    fun `getController() with default effect target arg returns controllers`() {
        val proxyEffect = scope.getProxy<EffectWithDefaultTarget>()
        val interfaceController = scope.getController<EffectWithDefaultTarget>()
        val implController = scope.getController<EffectWithDefaultTargetImpl>()
        val effectForInterface = spyk(EffectWithDefaultTargetImpl())
        val effectForImplementation = spyk(EffectWithDefaultTargetImpl())

        interfaceController.start(effectForInterface)
        proxyEffect.run("input1")
        verify(exactly = 1) {
            effectForInterface.run("input1")
        }
        verify(exactly = 0) {
            effectForImplementation.run(any())
        }

        interfaceController.stop()
        clearMocks(effectForInterface)
        implController.start(effectForImplementation)
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
        val proxyEffect = scope.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = scope.getController<EffectImpl>()
        controller.start(effectImpl)

        proxyEffect.unitRun("input")

        verify(exactly = 1) {
            effectImpl.unitRun("input")
        }
    }

    @Test
    fun `proxy with detached implementation delivers call after attaching`() {
        val proxyEffect = scope.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = scope.getController<EffectImpl>()

        proxyEffect.unitRun("input")
        verify(exactly = 0) {
            effectImpl.unitRun(any())
        }
        controller.start(effectImpl)
        verify(exactly = 1) {
            effectImpl.unitRun("input")
        }
    }

    @Test
    fun `proxy delivers call to last attached implementation`() {
        val proxyEffect = scope.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val controller1 = scope.getController<EffectImpl>()
        val controller2 = scope.getController<EffectImpl>()
        controller1.start(effectImpl1)
        controller2.start(effectImpl2)

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
        val proxyEffect = scope.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val controller = scope.getController<EffectImpl>()
        controller.start(effectImpl1)
        controller.start(effectImpl2)

        proxyEffect.unitRun("input")

        verify(exactly = 1) {
            effectImpl1.unitRun(any())
        }
        verify(exactly = 0) {
            effectImpl2.unitRun(any())
        }
    }

    @Test
    fun `proxy delivers one call to one implementation`() {
        val proxyEffect = scope.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val controller1 = scope.getController<EffectImpl>()
        val controller2 = scope.getController<EffectImpl>()

        proxyEffect.unitRun("input")
        controller1.start(effectImpl1)
        controller2.start(effectImpl2)

        verify(exactly = 1) {
            effectImpl1.unitRun("input")
        }
        verify(exactly = 0) {
            effectImpl2.unitRun(any())
        }
    }

    @Test
    fun `proxy does not deliver call to detached implementation`() {
        val proxyEffect = scope.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = scope.getController<EffectImpl>()
        controller.start(effectImpl)
        controller.stop()

        proxyEffect.unitRun("input")

        verify(exactly = 0) {
            effectImpl.unitRun(any())
        }
    }

    @Test
    fun `proxy delivers call after re-attaching implementation`() {
        val proxyEffect = scope.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = scope.getController<EffectImpl>()
        controller.start(effectImpl)
        controller.stop()

        proxyEffect.unitRun("input")
        controller.start(effectImpl)

        verify(exactly = 1) {
            effectImpl.unitRun("input")
        }
    }

    @Test
    fun `proxy ignores exception from unit call`() = runTest {
        val proxyEffect = scope.getProxy<Effect>()
        val effectImpl = mockk<Effect>()
        val controller = scope.getController<Effect>()
        coEvery { effectImpl.unitRun(any()) } throws IllegalStateException()
        controller.start(effectImpl)

        val result = runCatching {
            proxyEffect.unitRun("input")
        }

        assertTrue(result.isSuccess)
    }

    @Test
    fun `proxy returns result from suspend call`() = runTest {
        val proxyEffect = scope.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = scope.getController<EffectImpl>()
        controller.start(effectImpl)

        val result = proxyEffect.coroutineRun("input")

        assertEquals(expectedResult("input"), result)
    }

    @Test
    fun `proxy waits for result from suspend call`() = runFlowTest {
        val proxyEffect = this@CoreRuntimeIntegrationTest.scope.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = this@CoreRuntimeIntegrationTest.scope.getController<EffectImpl>()

        val result = executeInBackground {
            proxyEffect.coroutineRun("input")
        }

        // assert the job is still running after emit delay because
        // the effect implementation should not be called
        advanceTimeBy(EMIT_DELAY + 1)
        assertEquals(JobStatus.Executing, result.status)

        // attach the implementation, but the job is still running
        // before result is emitted
        controller.start(effectImpl)
        advanceTimeBy(EMIT_DELAY)
        assertEquals(JobStatus.Executing, result.status)

        // after the emit delay -> job done
        advanceTimeBy(1)
        assertEquals(JobStatus.Completed(expectedResult("input")), result.status)
    }

    @Test
    fun `proxy re-executes suspend call after re-attaching`() = runFlowTest {
        val proxyEffect = this@CoreRuntimeIntegrationTest.scope.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = this@CoreRuntimeIntegrationTest.scope.getController<EffectImpl>()
        controller.start(effectImpl)

        val result = executeInBackground {
            proxyEffect.coroutineRun("input")
        }

        // assert job is still running after detaching the implementation
        advanceTimeBy(EMIT_DELAY)
        controller.stop()
        advanceTimeBy(1)
        assertEquals(JobStatus.Executing, result.status)
        // re-attach the implementation, the job must be restarted from the beginning
        controller.start(effectImpl)
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
        val proxyEffect = this@CoreRuntimeIntegrationTest.scope.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl(byFirstEffect))
        val effectImpl2 = spyk(EffectImpl())
        val controller1 = this@CoreRuntimeIntegrationTest.scope.getController<EffectImpl>()
        val controller2 = this@CoreRuntimeIntegrationTest.scope.getController<EffectImpl>()
        controller1.start(effectImpl1)
        controller2.start(effectImpl2)

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
        controller2.stop()
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
        val proxyEffect = scope.getProxy<Effect>()
        val effectImpl = mockk<Effect>()
        val controller = scope.getController<Effect>()
        coEvery { effectImpl.coroutineRun(any()) } throws IllegalStateException()
        controller.start(effectImpl)

        val exception = runCatching {
            proxyEffect.coroutineRun("input")
        }.exceptionOrNull()

        assertTrue(exception is IllegalStateException)
    }

    @Test
    fun `proxy executes flow on all effects`() = runFlowTest {
        val byFirstEffect = "byFirstEffect"
        val bySecondEffect = "byFirstEffect"
        val proxyEffect = this@CoreRuntimeIntegrationTest.scope.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl(byFirstEffect))
        val effectImpl2 = spyk(EffectImpl(bySecondEffect))
        val controller1 = this@CoreRuntimeIntegrationTest.scope.getController<EffectImpl>()
        val controller2 = this@CoreRuntimeIntegrationTest.scope.getController<EffectImpl>()
        controller1.start(effectImpl1)
        controller2.start(effectImpl2)

        val state = proxyEffect.flowRun().startCollecting()

        // assert items from both effects are delivered
        effectImpl1.send("1")
        assertEquals(expectedResult("1", byFirstEffect), state.lastItem)
        effectImpl2.send("2")
        assertEquals(expectedResult("2", bySecondEffect), state.lastItem)
        // assert items are not delivered from detached effects
        controller1.stop()
        effectImpl1.send("3")
        assertEquals(expectedResult("2", bySecondEffect), state.lastItem)
        // assert items are delivered again after attaching
        controller1.start(effectImpl1)
        effectImpl1.send("4")
        assertEquals(expectedResult("4", byFirstEffect), state.lastItem)
    }

    @Test
    fun `proxy delivers exception from flow call and cancels listening`() = runFlowTest {
        val proxyEffect = this@CoreRuntimeIntegrationTest.scope.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val controller1 = this@CoreRuntimeIntegrationTest.scope.getController<Effect>()
        val controller2 = this@CoreRuntimeIntegrationTest.scope.getController<Effect>()
        controller1.start(effectImpl1)
        controller2.start(effectImpl2)

        val state = proxyEffect.flowRun().startCollecting()
        effectImpl1.fail(IllegalStateException())

        val exception = (state.collectStatus as CollectStatus.Failed).exception
        assertTrue(exception is IllegalStateException)
    }

    @Test
    fun `proxy completes result flow when all flows from all effects complete`() = runFlowTest {
        val proxyEffect = this@CoreRuntimeIntegrationTest.scope.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val controller1 = this@CoreRuntimeIntegrationTest.scope.getController<Effect>()
        val controller2 = this@CoreRuntimeIntegrationTest.scope.getController<Effect>()
        controller1.start(effectImpl1)
        controller2.start(effectImpl2)

        val state = proxyEffect.flowRun().startCollecting()

        effectImpl1.complete()
        assertEquals(CollectStatus.Collecting, state.collectStatus)
        effectImpl2.complete()
        assertEquals(CollectStatus.Completed, state.collectStatus)
    }

    @Test
    fun `proxy does not complete result flow if all effects are detached`() = runFlowTest {
        val proxyEffect = this@CoreRuntimeIntegrationTest.scope.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val controller1 = this@CoreRuntimeIntegrationTest.scope.getController<Effect>()
        val controller2 = this@CoreRuntimeIntegrationTest.scope.getController<Effect>()
        controller1.start(effectImpl1)
        controller2.start(effectImpl2)

        val state = proxyEffect.flowRun().startCollecting()

        // detach the first effect -> still collecting
        controller1.stop()
        assertEquals(CollectStatus.Collecting, state.collectStatus)
        // detach the second effect -> continue collecting anyway
        controller2.stop()
        assertEquals(CollectStatus.Collecting, state.collectStatus)
        // attach the first effect again -> items must be delivered
        controller1.start(effectImpl1)
        effectImpl1.send("input")
        assertEquals(expectedResult("input"), state.lastItem)
        // complete the first effect -> collecting finished, because
        // only 1 effect is attached
        effectImpl1.complete()
        assertEquals(CollectStatus.Completed, state.collectStatus)
    }

    @Test
    fun `two suspend calls should not interfere`() = runFlowTest {
        val proxyEffect1 = this@CoreRuntimeIntegrationTest.scope.getProxy<Effect>()
        val proxyEffect2 = this@CoreRuntimeIntegrationTest.scope.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = this@CoreRuntimeIntegrationTest.scope.getController<Effect>()
        controller.start(effectImpl)

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
        val proxy = this@CoreRuntimeIntegrationTest.scope.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = this@CoreRuntimeIntegrationTest.scope.getController<Effect>()

        proxy.unitRun("input")
        (proxy as AutoCloseable).close()
        controller.start(effectImpl)

        verify(exactly = 0) {
            effectImpl.unitRun(any())
        }
    }

    @Test
    fun `non-overridden close() call is not delivered to effect implementation`() {
        val proxy = scope.getProxy<EffectWithNonOverriddenClose>()
        val effectImpl = spyk(EffectWithNonOverriddenCloseImpl())
        val controller = scope.getController<EffectWithNonOverriddenClose>()

        proxy.run("input")
        proxy.close()
        controller.start(effectImpl)

        verify(exactly = 0) {
            effectImpl.run(any())
            effectImpl.close()
        }
    }

    @Test
    fun `overridden close() call is not delivered to effect implementation`() {
        val proxy = scope.getProxy<EffectWithNonOverriddenClose>()
        val effectImpl = spyk(EffectWithNonOverriddenCloseImpl())
        val controller = scope.getController<EffectWithNonOverriddenClose>()

        proxy.run("input")
        proxy.close()
        controller.start(effectImpl)

        verify(exactly = 0) {
            effectImpl.run(any())
            effectImpl.close()
        }
    }

    @Test
    fun `combined effect can return controller for any interface`() {
        val proxy1 = scope.getProxy<Effect1>()
        val proxy2 = scope.getProxy<Effect2>()
        val combinedEffect = spyk(CombinedEffect())

        val controller1 = scope.getController<Effect1>()
        val controller2 = scope.getController<Effect2>()
        controller1.start(combinedEffect)
        controller2.start(combinedEffect)
        proxy1.run1("input")
        proxy2.run2("input")

        verify(exactly = 1) {
            combinedEffect.run1("input")
            combinedEffect.run2("input")
        }
    }

    @Test
    fun `interface controller of combined effect connects only to own interface`() {
        val proxy = scope.getProxy<Effect1>()
        val combinedEffect = spyk(CombinedEffect())
        val controller1 = scope.getController<Effect1>()
        val controller2 = scope.getController<Effect2>()

        controller2.start(combinedEffect)
        proxy.run1("input")
        verify(exactly = 0) {
            combinedEffect.run1(any())
        }

        controller1.start(combinedEffect)
        verify(exactly = 1) {
            combinedEffect.run1("input")
        }
    }

    @Test
    fun `class controller of combined effect connects to all target interfaces`() {
        val proxy1 = scope.getProxy<Effect1>()
        val proxy2 = scope.getProxy<Effect2>()
        val combinedEffect = spyk(CombinedEffect())
        val controller = scope.getController<CombinedEffect>()

        controller.start(combinedEffect)
        proxy1.run1("input")
        proxy2.run2("input")

        verify(exactly = 1) {
            combinedEffect.run1("input")
            combinedEffect.run2("input")
        }
    }

    @Test
    fun `class controller of combined effect disconnects from all target interfaces`() {
        val proxy1 = scope.getProxy<Effect1>()
        val proxy2 = scope.getProxy<Effect2>()
        val combinedEffect = spyk(CombinedEffect())
        val controller = scope.getController<CombinedEffect>()

        controller.start(combinedEffect)
        controller.stop()
        proxy1.run1("input")
        proxy2.run2("input")

        verify(exactly = 0) {
            combinedEffect.run1(any())
            combinedEffect.run2(any())
        }
    }

    @Test
    fun `class controller of combined effect gathers interfaces from scope hierarchy`() {
        val childScope = RootEffectScopes.empty
            .createChild(
                ManagedInterfaces.ListOf(Effect1::class),
                proxyEffectFactory = RuntimeProxyEffectFactory()
            )
            .createChild(
                ManagedInterfaces.ListOf(Effect2::class),
            )
        val proxy1 = childScope.getProxy(Effect1::class)
        val proxy2 = childScope.getProxy(Effect2::class)
        val combinedEffect = spyk(CombinedEffect())
        val controller = childScope.getController<CombinedEffect>()
        controller.start(combinedEffect)

        proxy1.run1("input")
        proxy2.run2("input")

        verify(exactly = 1) {
            combinedEffect.run1("input")
            combinedEffect.run2("input")
        }
    }

    @Test
    fun `class controller of combined effect with missing at least 1 interface fails`() {
        val childScope = RootEffectScopes.empty
            .createChild(
                ManagedInterfaces.ListOf(Effect1::class),
            )
        childScope.getProxy(Effect1::class)

        val result = runCatching {
            childScope.getController<CombinedEffect>()
        }

        assertTrue(result.exceptionOrNull() is EffectNotFoundException)
    }

    @Test
    fun `combined effect with target arg excludes non-listed interfaces`() {
        val proxy1 = scope.getProxy<TargetEffect1>()
        val proxy2 = scope.getProxy<TargetEffect2>()
        val proxy3 = scope.getProxy<NonTargetEffect3>()
        val combinedEffect = spyk(CombinedEffectWithTarget())
        val controller = scope.getController<CombinedEffectWithTarget>()
        controller.start(combinedEffect)

        proxy1.run1("input")
        proxy2.run2("input")
        proxy3.run3("input")

        verify(exactly = 1) {
            combinedEffect.run1("input")
            combinedEffect.run2("input")
        }
        verify(exactly = 0) {
            combinedEffect.run3(any())
        }
    }

}
