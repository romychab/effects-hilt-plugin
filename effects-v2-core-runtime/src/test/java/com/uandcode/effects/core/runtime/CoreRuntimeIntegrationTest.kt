package com.uandcode.effects.core.runtime

import com.uandcode.effects.core.EffectComponent
import com.uandcode.effects.core.RootEffectComponents
import com.uandcode.effects.core.getController
import com.uandcode.effects.core.getProxy
import com.uandcode.effects.core.mocks.Effect
import com.uandcode.effects.core.mocks.Effect.Companion.EMIT_DELAY
import com.uandcode.effects.core.mocks.Effect.Companion.expectedResult
import com.uandcode.effects.core.mocks.EffectImpl
import com.uandcode.effects.core.mocks.EffectWithCleanUp
import com.uandcode.effects.core.mocks.EffectWithCleanUpImpl
import com.uandcode.effects.core.mocks.EffectWithDefaultCleanUp
import com.uandcode.effects.core.mocks.EffectWithDefaultCleanUpImpl
import com.uandcode.effects.core.mocks.EffectWithDefaultTarget
import com.uandcode.effects.core.mocks.EffectWithDefaultTargetImpl
import com.uandcode.effects.core.mocks.EffectWithTarget
import com.uandcode.effects.core.mocks.EffectWithTargetImpl
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

    private lateinit var component: EffectComponent

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        RootEffectComponents.setGlobal(RuntimeEffectComponents.create())
        component = RootEffectComponents.global
    }

    @After
    fun tearDown() {
        RootEffectComponents.resetGlobalComponent()
    }

    @Test
    fun `getController() returns controllers for interface and implementation`() {
        val proxyEffect = component.getProxy<Effect>()
        val interfaceController = component.getController<Effect>()
        val implController = component.getController<EffectImpl>()
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
        val proxyEffect = component.getProxy<EffectWithTarget>()
        val interfaceController = component.getController<EffectWithTarget>()
        val implController = component.getController<EffectWithTargetImpl>()
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
        val proxyEffect = component.getProxy<EffectWithDefaultTarget>()
        val interfaceController = component.getController<EffectWithDefaultTarget>()
        val implController = component.getController<EffectWithDefaultTargetImpl>()
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
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = component.getController<EffectImpl>()
        controller.start(effectImpl)

        proxyEffect.unitRun("input")

        verify(exactly = 1) {
            effectImpl.unitRun("input")
        }
    }

    @Test
    fun `proxy with detached implementation delivers call after attaching`() {
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = component.getController<EffectImpl>()

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
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val controller1 = component.getController<EffectImpl>()
        val controller2 = component.getController<EffectImpl>()
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
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val controller = component.getController<EffectImpl>()
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
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val controller1 = component.getController<EffectImpl>()
        val controller2 = component.getController<EffectImpl>()

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
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = component.getController<EffectImpl>()
        controller.start(effectImpl)
        controller.stop()

        proxyEffect.unitRun("input")

        verify(exactly = 0) {
            effectImpl.unitRun(any())
        }
    }

    @Test
    fun `proxy delivers call after re-attaching implementation`() {
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = component.getController<EffectImpl>()
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
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl = mockk<Effect>()
        val controller = component.getController<Effect>()
        coEvery { effectImpl.unitRun(any()) } throws IllegalStateException()
        controller.start(effectImpl)

        val result = runCatching {
            proxyEffect.unitRun("input")
        }

        assertTrue(result.isSuccess)
    }

    @Test
    fun `proxy returns result from suspend call`() = runTest {
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = component.getController<EffectImpl>()
        controller.start(effectImpl)

        val result = proxyEffect.coroutineRun("input")

        assertEquals(expectedResult("input"), result)
    }

    @Test
    fun `proxy waits for result from suspend call`() = runFlowTest {
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = component.getController<EffectImpl>()

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
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = component.getController<EffectImpl>()
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
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl(byFirstEffect))
        val effectImpl2 = spyk(EffectImpl())
        val controller1 = component.getController<EffectImpl>()
        val controller2 = component.getController<EffectImpl>()
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
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl = mockk<Effect>()
        val controller = component.getController<Effect>()
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
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl(byFirstEffect))
        val effectImpl2 = spyk(EffectImpl(bySecondEffect))
        val controller1 = component.getController<EffectImpl>()
        val controller2 = component.getController<EffectImpl>()
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
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val controller1 = component.getController<Effect>()
        val controller2 = component.getController<Effect>()
        controller1.start(effectImpl1)
        controller2.start(effectImpl2)

        val state = proxyEffect.flowRun().startCollecting()
        effectImpl1.fail(IllegalStateException())

        val exception = (state.collectStatus as CollectStatus.Failed).exception
        assertTrue(exception is IllegalStateException)
    }

    @Test
    fun `proxy completes result flow when all flows from all effects complete`() = runFlowTest {
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val controller1 = component.getController<Effect>()
        val controller2 = component.getController<Effect>()
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
        val proxyEffect = component.getProxy<Effect>()
        val effectImpl1 = spyk(EffectImpl())
        val effectImpl2 = spyk(EffectImpl())
        val controller1 = component.getController<Effect>()
        val controller2 = component.getController<Effect>()
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
        val proxyEffect1 = component.getProxy<Effect>()
        val proxyEffect2 = component.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = component.getController<Effect>()
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
        val proxy = component.getProxy<Effect>()
        val effectImpl = spyk(EffectImpl())
        val controller = component.getController<Effect>()

        proxy.unitRun("input")
        (proxy as AutoCloseable).close()
        controller.start(effectImpl)

        verify(exactly = 0) {
            effectImpl.unitRun(any())
        }
    }

    @Test
    fun `default cleanUp call is not delivered to effect implementation`() {
        val proxy = component.getProxy<EffectWithDefaultCleanUp>()
        val effectImpl = spyk(EffectWithDefaultCleanUpImpl())
        val controller = component.getController<EffectWithDefaultCleanUp>()

        proxy.run("input")
        proxy.cleanUp()
        controller.start(effectImpl)

        verify(exactly = 0) {
            effectImpl.run(any())
            effectImpl.cleanUp()
        }
    }

    @Test
    fun `non-default cleanUp call is not delivered to effect implementation`() {
        val proxy = component.getProxy<EffectWithCleanUp>()
        val effectImpl = spyk(EffectWithCleanUpImpl())
        val controller = component.getController<EffectWithCleanUp>()

        proxy.run("input")
        proxy.destroy()
        controller.start(effectImpl)

        verify(exactly = 0) {
            effectImpl.run(any())
            effectImpl.destroy()
        }
    }

}
