package com.elveum.effects.core.v2.impl

import com.elveum.effects.core.v2.ObservableResourcesStore
import com.elveum.effects.core.v2.ObservableResourcesStore.ResourceObserver

public class ObservableResourcesStoreImpl<Resource> : ObservableResourcesStore<Resource> {

    private val resources = LinkedHashSet<Resource>()
    private val observers = LinkedHashSet<ResourceObserver<Resource>>()

    override val currentAttachedResources: Iterable<Resource>
        get() = resources.toList()

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