package com.uandcode.effects.core.internal

import com.uandcode.effects.core.internal.ObservableResourceStore.ResourceObserver

internal class ObservableResourceStoreImpl<Resource> : ObservableResourceStore<Resource> {

    private val resources = LinkedHashSet<Resource>()
    private val observers = LinkedHashSet<ResourceObserver<Resource>>()

    override val currentAttachedResources: Iterable<Resource>
        get() = (resources as Iterable<Resource>).reversed()

    private val currentObservers: List<ResourceObserver<Resource>>
        get() = observers.toList()

    override fun attachResource(resource: Resource) {
        if (resources.add(resource)) {
            currentObservers.forEach { observer ->
                observer.onResourceAttached(resource)
            }
        }
    }

    override fun detachResource(resource: Resource) {
        if (resources.remove(resource)) {
            currentObservers.forEach { it.onResourceDetached(resource) }
        }
    }

    override fun addObserver(observer: ResourceObserver<Resource>) {
        if (observers.add(observer)) {
            currentAttachedResources.forEach { resource ->
                if (observers.contains(observer)) {
                    observer.onResourceAttached(resource)
                }
            }
        }
    }

    override fun removeObserver(observer: ResourceObserver<Resource>) {
        if (observers.remove(observer)) {
            observer.onObserverRemoved()
        }
    }

    override fun removeAllObservers() {
        val currentObservers = this.currentObservers
        observers.clear()
        currentObservers.forEach { it.onObserverRemoved() }
    }

}
