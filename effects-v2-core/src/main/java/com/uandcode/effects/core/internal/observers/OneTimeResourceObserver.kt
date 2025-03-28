package com.uandcode.effects.core.internal.observers

import com.uandcode.effects.core.ObservableResourceStore
import com.uandcode.effects.core.ObservableResourceStore.ResourceObserver

internal class OneTimeResourceObserver<Resource>(
    private val store: ObservableResourceStore<Resource>,
    private val origin: ResourceObserver<Resource>,
    private val observerRemovalListener: ObserverRemovalListener<Resource>,
) : ResourceObserver<Resource> by origin {

    override fun onResourceAttached(resource: Resource) {
        try {
            origin.onResourceAttached(resource)
        } finally {
            store.removeObserver(this)
        }
    }

    override fun onObserverRemoved() {
        observerRemovalListener.onObserverRemoved(this)
    }

    fun interface ObserverRemovalListener<Resource> {
        fun onObserverRemoved(observer: ResourceObserver<Resource>)
    }
}
