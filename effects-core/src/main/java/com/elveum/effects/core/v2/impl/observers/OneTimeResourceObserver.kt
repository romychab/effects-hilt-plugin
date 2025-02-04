package com.elveum.effects.core.v2.impl.observers

import com.elveum.effects.core.v2.ObservableResourceStore
import com.elveum.effects.core.v2.ObservableResourceStore.ResourceObserver

internal class OneTimeResourceObserver<Resource>(
    private val store: ObservableResourceStore<Resource>,
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

