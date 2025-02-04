package com.elveum.effects.core.v2.impl

import com.elveum.effects.core.v2.CommandExecutor
import com.elveum.effects.core.v2.ObservableResourceStore
import com.elveum.effects.core.v2.impl.observers.CoroutineCommandObserver
import com.elveum.effects.core.v2.impl.observers.FlowCommandObserver
import com.elveum.effects.core.v2.impl.observers.OneTimeResourceObserver
import com.elveum.effects.core.v2.impl.observers.SimpleCommandObserver
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

public class CommandExecutorImpl<Resource>(
    private val resourceStore: ObservableResourceStore<Resource>,
) : CommandExecutor<Resource> {

    override fun execute(command: (Resource) -> Unit) {
        val observer = SimpleCommandObserver(command)
        val oneTimeObserver = OneTimeResourceObserver(resourceStore, observer)
        resourceStore.addObserver(oneTimeObserver)
    }

    override suspend fun <T> executeCoroutine(command: suspend (Resource) -> T): T {
        return coroutineScope {
            val scope = this
            val completableDeferred = CompletableDeferred<T>()
            val observer = CoroutineCommandObserver(
                scope = scope,
                completableDeferred = completableDeferred,
                command = command,
                currentActiveResourcesProvider = { resourceStore.currentAttachedResources }
            )
            try {
                resourceStore.addObserver(observer)
                completableDeferred.await()
            } finally {
                resourceStore.removeObserver(observer)
            }
        }
    }

    override fun <T> executeFlow(command: (Resource) -> Flow<T>): Flow<T> {
        return channelFlow {
            val scope: CoroutineScope = this
            val channel: SendChannel<T> = this
            val flowCommandObserver = FlowCommandObserver(
                scope = scope,
                channel = channel,
                command = command,
            )
            resourceStore.addObserver(flowCommandObserver)
            awaitClose {
                resourceStore.removeObserver(flowCommandObserver)
            }
        }
    }

}