package com.elveum.effects.core

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume

typealias UnitCommand<R> = (R) -> Unit
typealias CoroutineCommand<R, T> = suspend (R) -> T
typealias FlowCommand<R, T> = (R) -> Flow<T>

/**
 * For internal usage.
 *
 * Used by side-effect mediators for proper delivering of side-effects
 * from view-models to implementations (to the activity).
 */
class CommandProcessor<R>(
    private val coroutineScope: CoroutineScope
) {

    var resource: R?
        get() = resourceHolder?.resource
        set(value) {
            resourceHolder = if (value == null) {
                null
            } else {
                Holder(
                    resource = value,
                    scope = MainScope()
                )
            }
        }

    private var resourceHolder: Holder<R>? = null
        set(value) {
            if (!coroutineScope.isActive) return
            val oldValue = field
            field = value
            oldValue?.scope?.cancel(ResourceScopeCancelledException())
            if (value != null) {
                unitCommands.forEach { it(value) }
                unitCommands.clear()
            }
        }

    private val unitCommands = mutableListOf<UnitCommand<Holder<R>>>()

    init {
        coroutineScope.launch {
            try {
                awaitCancellation()
            } finally {
                resourceHolder?.scope?.cancel()
                resourceHolder = null
                unitCommands.clear()
            }
        }
    }

    fun submit(command: UnitCommand<R>) {
        doSubmit {
            command(it.resource)
        }
    }

    fun <T> submitFlow(command: FlowCommand<R, T>): Flow<T> {
        return callbackFlow {
            while (true) {
                val holder = awaitResource()
                val job = holder.scope.launch {
                    val flow = command.invoke(holder.resource)
                    flow.collect {
                        send(it)
                    }
                }
                try {
                    job.join()
                    break
                } catch (e: ResourceScopeCancelledException) {
                    continue
                } catch (e: Exception) {
                    cancel("Flow has been failed with error", e)
                    break
                }
            }
        }.conflate()
    }

    suspend fun <T> submitCoroutine(command: CoroutineCommand<R, T>): T {
        while (true) {
            val holder = awaitResource()
            try {
                val deferred = holder.scope.async {
                    command.invoke(holder.resource)
                }
                return deferred.await()
            } catch (e: ResourceScopeCancelledException) {
                continue
            }
        }
    }

    private suspend fun awaitResource(): Holder<R> = suspendCancellableCoroutine { continuation ->
        doSubmit {
            continuation.resume(it)
        }
    }

    private fun doSubmit(command: (Holder<R>) -> Unit) {
        if (!coroutineScope.isActive) return

        resourceHolder?.let {
            command(it)
            return
        }
        unitCommands.add(command)
    }

    class Holder<R>(
        val resource: R,
        val scope: CoroutineScope
    )

}