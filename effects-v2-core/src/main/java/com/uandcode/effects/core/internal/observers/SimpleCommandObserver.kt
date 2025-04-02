package com.uandcode.effects.core.internal.observers

import com.uandcode.effects.core.internal.ObservableResourceStore.ResourceObserver

internal class SimpleCommandObserver<Resource>(
    private val command: (Resource) -> Unit,
) : ResourceObserver<Resource> {

    override fun onResourceAttached(resource: Resource) {
        try {
            command(resource)
        } catch (e: Exception) {
            e.printStackTrace(System.err)
        }
    }

    override fun onResourceDetached(resource: Resource) {
    }

    override fun onObserverRemoved() {
    }
}
