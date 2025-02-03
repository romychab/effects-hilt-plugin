@file:OptIn(ExperimentalCoroutinesApi::class)

package com.elveum.effects.core.v2.impl

import com.elveum.effects.core.v2.CommandExecutor
import com.elveum.effects.core.v2.ObservableResourcesStore
import com.elveum.effects.core.v2.ObservableResourcesStore.ResourceObserver
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CommandExecutorIntegrationTest {

    private val coroutineCommandTimeout = 1000L

    private lateinit var resources: TestObservableResourcesStore

    lateinit var commandExecutor: CommandExecutor<String>

    @RelaxedMockK
    lateinit var simpleCommand: (String) -> Unit

    lateinit var coroutineCommand: suspend (String) -> String

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        resources = TestObservableResourcesStore(ObservableResourcesStoreImpl())
        commandExecutor = CommandExecutorImpl(resources)
        coroutineCommand = mockk()
        coEvery { coroutineCommand.invoke(any()) } coAnswers {
            delay(coroutineCommandTimeout)
            expectedResult(firstArg()).getOrThrow()
        }
    }

    @Test
    fun `execute should call command only once`() {
        resources.attachResource("Resource1")
        resources.attachResource("Resource2")

        commandExecutor.execute(simpleCommand)

        verify(exactly = 1) {
            simpleCommand("Resource1")
        }
        verify(exactly = 0) {
            simpleCommand("Resource2")
        }
    }

    @Test
    fun `execute without resources should not call command`() {
        commandExecutor.execute(simpleCommand)

        verify(exactly = 0) {
            simpleCommand(any())
        }
    }


    @Test
    fun `attachResource after execute should call command once`() {
        commandExecutor.execute(simpleCommand)

        resources.attachResource("Resource1")
        resources.attachResource("Resource2")

        verify(exactly = 1) {
            simpleCommand("Resource1")
        }
        verify(exactly = 0) {
            simpleCommand("Resource2")
        }
    }

    @Test
    fun `execute after detach should not call command`() {
        resources.attachResource("Resource1")
        resources.detachResource("Resource1")

        commandExecutor.execute(simpleCommand)

        verify(exactly = 0) {
            simpleCommand("Resource1")
        }
    }

    @Test
    fun `execute after attach of new resource should call command with new resource`() {
        resources.attachResource("Resource1")
        resources.detachResource("Resource1")

        commandExecutor.execute(simpleCommand)
        resources.attachResource("Resource2")

        verify(exactly = 1) {
            simpleCommand("Resource2")
        }
    }

    @Test
    fun `executeCoroutine with available resource should execute command immediately`() = runTest {
        resources.attachResource("Resource1")

        val state = executeCoroutineInBackground(coroutineCommand)

        // assert initial state (command started)
        coVerify(exactly = 1) {
            coroutineCommand.invoke("Resource1")
        }
        assertNull(state.result)
        // assert command finished
        advanceTimeBy(coroutineCommandTimeout + 1)
        assertEquals(expectedResult("Resource1"), state.result)
    }

    @Test
    fun `executeCoroutine without resource should not execute command`() = runTest {
        val state = executeCoroutineInBackground(coroutineCommand)

        advanceTimeBy(coroutineCommandTimeout + 1)
        assertNull(state.result)
    }

    @Test
    fun `executeCoroutine after attach of resource should execute command`() = runTest {
        val state = executeCoroutineInBackground(coroutineCommand)

        advanceTimeBy(2000)
        resources.attachResource("Resource1")

        // assert initial state (command started)
        coVerify(exactly = 1) {
            coroutineCommand.invoke("Resource1")
        }
        assertNull(state.result)
        // assert command finished
        advanceTimeBy(coroutineCommandTimeout + 1)
        assertEquals(expectedResult("Resource1"), state.result)
    }

    @Test
    fun `executeCoroutine with failed command should rethrow exception`() = runTest {
        val expectedException = IllegalStateException("123")
        coroutineCommand = {
            delay(coroutineCommandTimeout)
            throw expectedException
        }
        resources.attachResource("Resource1")

        val state = executeCoroutineInBackground(coroutineCommand)
        advanceTimeBy(coroutineCommandTimeout + 1)

        assertEquals(expectedException.message, state.result?.exceptionOrNull()?.message)
        assertTrue(state.result?.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `executeCoroutine with detached resource should not cancel command`() = runTest {
        resources.attachResource("Resource1")

        val state = executeCoroutineInBackground(coroutineCommand)
        advanceTimeBy(coroutineCommandTimeout)
        resources.detachResource("Resource1")
        advanceTimeBy(coroutineCommandTimeout + 1)

        assertNull(state.result)
    }

    @Test
    fun `executeCoroutine with re-attached resource should re-execute command`() = runTest {
        resources.attachResource("Resource1")
        val state = executeCoroutineInBackground(coroutineCommand)
        advanceTimeBy(coroutineCommandTimeout)
        resources.detachResource("Resource1")

        // attach next resource -> command should be re-executed with new resource
        resources.attachResource("Resource2")
        advanceTimeBy(coroutineCommandTimeout + 1)

        assertEquals(expectedResult("Resource2"), state.result)
    }

    @Test
    fun `executeCoroutine with multiple resources should re-execute command to last resource`() = runTest {
        resources.attachResource("Resource1")
        resources.attachResource("Resource2")
        resources.attachResource("Resource3")
        val state = executeCoroutineInBackground(coroutineCommand)
        advanceTimeBy(coroutineCommandTimeout)

        resources.detachResource("Resource1")
        advanceTimeBy(coroutineCommandTimeout + 1)

        assertEquals(expectedResult("Resource3"), state.result)
    }

    @Test
    fun `executeCoroutine with multiple resources should execute command only on first resource`() = runTest {
        resources.attachResource("Resource1")
        resources.attachResource("Resource2")
        resources.attachResource("Resource3")

        val state = executeCoroutineInBackground(coroutineCommand)
        advanceTimeBy(coroutineCommandTimeout + 1)

        assertEquals(expectedResult("Resource1"), state.result)
        coVerify(exactly = 0) {
            coroutineCommand.invoke("Resource2")
            coroutineCommand.invoke("Resource3")
        }
    }

    @Test
    fun `executeCoroutine with cancelled origin command should release resources`() = runTest {
        resources.attachResource("Resource1")
        val state = executeCoroutineInBackground(coroutineCommand)

        advanceTimeBy(coroutineCommandTimeout)
        state.job?.cancel()

        resources.assertAllObserversAreRemoved()
    }

    private fun expectedResult(input: String) = Result.success("processed-$input")

    private fun TestScope.executeCoroutineInBackground(command: suspend (String) -> String): State {
        val state = State()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            state.result = kotlin.runCatching {
                commandExecutor.executeCoroutine(command)
            }
        }
        state.job = job
        return state
    }

    private class State(
        var result: Result<String>? = null,
        var job: Job? = null
    )

    private class TestObservableResourcesStore(
        private val origin: ObservableResourcesStore<String>
    ) : ObservableResourcesStore<String> by origin {

        val trackedObservers = mutableSetOf<ResourceObserver<String>>()

        override fun addObserver(observer: ResourceObserver<String>) {
            trackedObservers.add(observer)
            origin.addObserver(observer)
        }

        override fun removeObserver(observer: ResourceObserver<String>) {
            trackedObservers.remove(observer)
            origin.removeObserver(observer)
        }

        fun assertAllObserversAreRemoved() {
            assertTrue(trackedObservers.isEmpty())
        }

    }

}