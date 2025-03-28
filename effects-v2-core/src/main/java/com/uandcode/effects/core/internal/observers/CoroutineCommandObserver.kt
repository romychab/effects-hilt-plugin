package com.uandcode.effects.core.internal.observers

import com.uandcode.effects.core.ObservableResourceStore.ResourceObserver
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class CoroutineCommandObserver<Resource, T>(
    private val scope: CoroutineScope,
    private val completableDeferred: CompletableDeferred<T>,
    private val currentActiveResourcesProvider: CurrentActiveResourcesProvider<Resource>,
    private val command: suspend (Resource) -> T,
) : ResourceObserver<Resource> {

    private var currentResource: Resource? = null
    private var job: Job? = null
    private var isFinished = false

    override fun onResourceAttached(resource: Resource) {
        if (currentResource != null) return
        if (isFinished) return
        currentResource = resource
        job = scope.launch {
            try {
                val result = command(resource)
                completableDeferred.complete(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                completableDeferred.completeExceptionally(e)
            }
        }
    }

    override fun onResourceDetached(resource: Resource) {
        if (currentResource != resource) return
        currentResource = null
        job?.cancel()
        job = null
        currentActiveResourcesProvider
            .getCurrentActiveResources()
            .firstOrNull()
            ?.let(::onResourceAttached)
    }

    override fun onObserverRemoved() {
        isFinished = true
        currentResource = null
        job?.cancel()
        job = null
    }

    fun interface CurrentActiveResourcesProvider<Resource> {
        fun getCurrentActiveResources(): Iterable<Resource>
    }
}
