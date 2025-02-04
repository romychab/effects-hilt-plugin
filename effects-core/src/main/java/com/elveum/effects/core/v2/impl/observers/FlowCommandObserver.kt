package com.elveum.effects.core.v2.impl.observers

import com.elveum.effects.core.v2.ObservableResourceStore.ResourceObserver
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal class FlowCommandObserver<Resource, T>(
    private val scope: CoroutineScope,
    private val channel: SendChannel<T>,
    private val command: (Resource) -> Flow<T>,
) : ResourceObserver<Resource> {

    private var isFinished = false
    private val jobMap = mutableMapOf<Resource, Job>()

    override fun onResourceAttached(resource: Resource) {
        if (jobMap.contains(resource)) return
        if (isFinished) return
        jobMap[resource] = scope.launch {
            try {
                val flow = command.invoke(resource)
                flow.collect(channel::send)
                finishIfLastFlowCompleted()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                isFinished = true
                channel.close(e)
                jobMap.values.forEach { it.cancel() }
            } finally {
                jobMap.remove(resource)
            }
        }
    }

    override fun onResourceDetached(resource: Resource) {
        if (isFinished) return
        jobMap.remove(resource)?.cancel()
    }

    override fun onObserverRemoved() {
        isFinished = true
        jobMap.clear()
    }

    private fun finishIfLastFlowCompleted() {
        if (jobMap.size == 1) {
            isFinished = true
            channel.close()
        }
    }

}