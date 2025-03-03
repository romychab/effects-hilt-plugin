package com.elveum.effects.core.impl.observers

import android.util.Log
import com.elveum.effects.core.ObservableResourceStore.ResourceObserver

internal class SimpleCommandObserver<Resource>(
    private val command: (Resource) -> Unit,
) : ResourceObserver<Resource> {

    override fun onResourceAttached(resource: Resource) {
        try {
            command(resource)
        } catch (e: Exception) {
            Log.e("CommandExecutor", "Failed to execute one-shot command", e)
        }
    }

    override fun onResourceDetached(resource: Resource) {
    }

    override fun onObserverRemoved() {
    }
}