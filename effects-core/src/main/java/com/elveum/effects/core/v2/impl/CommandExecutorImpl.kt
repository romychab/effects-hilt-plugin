package com.elveum.effects.core.v2.impl

import com.elveum.effects.core.v2.CommandExecutor
import com.elveum.effects.core.v2.ObservableResourcesStore
import com.elveum.effects.core.v2.impl.observers.CoroutineCommandObserver
import com.elveum.effects.core.v2.impl.observers.OneTimeResourceObserver
import com.elveum.effects.core.v2.impl.observers.SimpleCommandObserver
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

public class CommandExecutorImpl<Resource>(
    private val resourcesStore: ObservableResourcesStore<Resource>,
) : CommandExecutor<Resource> {

    override fun execute(command: (Resource) -> Unit) {
        val observer = SimpleCommandObserver(command)
        val oneTimeObserver = OneTimeResourceObserver(resourcesStore, observer)
        resourcesStore.addObserver(oneTimeObserver)
    }

    override suspend fun <T> executeCoroutine(command: suspend (Resource) -> T): T {
        return coroutineScope {
            val scope = this
            val completableDeferred = CompletableDeferred<T>()
            val observer = CoroutineCommandObserver(
                scope = scope,
                completableDeferred = completableDeferred,
                command = command,
                currentActiveResourcesProvider = { resourcesStore.currentAttachedResources }
            )
            try {
                resourcesStore.addObserver(observer)
                completableDeferred.await()
            } finally {
                resourcesStore.removeObserver(observer)
            }
        }
    }

    override fun <T> executeFlow(command: (Resource) -> Flow<T>): Flow<T> {
        TODO("Not yet implemented")
    }

}