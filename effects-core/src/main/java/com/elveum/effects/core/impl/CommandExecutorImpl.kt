package com.elveum.effects.core.impl

import com.elveum.effects.core.CommandExecutor
import com.elveum.effects.core.ObservableResourceStore
import com.elveum.effects.core.ObservableResourceStore.ResourceObserver
import com.elveum.effects.core.impl.observers.CoroutineCommandObserver
import com.elveum.effects.core.impl.observers.FlowCommandObserver
import com.elveum.effects.core.impl.observers.OneTimeResourceObserver
import com.elveum.effects.core.impl.observers.OneTimeResourceObserver.ObserverRemovalListener
import com.elveum.effects.core.impl.observers.SimpleCommandObserver
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

public class CommandExecutorImpl<Resource>(
    private val resourceStore: ObservableResourceStore<Resource>,
) : CommandExecutor<Resource>, ObserverRemovalListener<Resource> {

    private val activeUnitCommandObservers = mutableSetOf<ResourceObserver<Resource>>()

    override fun execute(command: (Resource) -> Unit) {
        val observer = SimpleCommandObserver(command)
        val oneTimeObserver = OneTimeResourceObserver(resourceStore, observer, this)
        activeUnitCommandObservers.add(oneTimeObserver)
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

    override fun cleanUp() {
        this.activeUnitCommandObservers.toList()
            .forEach(resourceStore::removeObserver)
        this.activeUnitCommandObservers.clear()
    }

    override fun onObserverRemoved(observer: ResourceObserver<Resource>) {
        this.activeUnitCommandObservers.remove(observer)
    }

}