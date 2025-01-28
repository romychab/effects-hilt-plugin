package com.elveum.effects.core.v2

import kotlinx.coroutines.flow.Flow

public interface CommandExecutor<Resource> {

    public fun execute(command: (Resource) -> Unit)

    public suspend fun <T> executeCoroutine(
        command: suspend (Resource) -> T
    ): T

    public fun <T> executeFlow(
        command: (Resource) -> Flow<T>
    ): Flow<T>

}