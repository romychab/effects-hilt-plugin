package com.elveum.effects.core.v2.impl.observers

import com.elveum.effects.core.v2.ObservableResourcesStore
import com.elveum.effects.core.v2.ObservableResourcesStore.ResourceObserver

internal class OneTimeResourceObserver<Resource>(
    private val store: ObservableResourcesStore<Resource>,
    private val origin: ResourceObserver<Resource>,
) : ResourceObserver<Resource> by origin {

    override fun onResourceAttached(resource: Resource) {
        try {
            origin.onResourceAttached(resource)
        } finally {
            store.removeObserver(this)
        }
    }

}

