package com.uandcode.effects.core

import com.uandcode.effects.stub.api.ProxyDependency
import kotlinx.coroutines.flow.Flow

/**
 * An interface for executing commands on a resource, which is used as a dependency
 * by all generated proxy classes.
 *
 * Commands are not executed immediately if there are no available resources;
 * instead, they are added to a queue and executed once a resource becomes available.
 *
 * @param Resource The type of resource on which commands are executed.
 */
public interface CommandExecutor<Resource> : ProxyDependency {

    /**
     * Executes a simple command on the given resource.
     *
     * If no resource is available, the command is queued for later execution.
     *
     * @param command The command to execute, represented as a function that takes a resource.
     */
    public fun execute(command: (Resource) -> Unit)

    /**
     * Executes a suspending command on the given resource and returns the result
     * of the execution.
     *
     * If no resource is available, the command is queued for later execution.
     * The command can be executed more than once if the previous execution
     * has been cancelled due to detaching of a resource. In this case the command
     * is added again to the queue and starts waiting for the next resource.
     *
     * In case, if more than one resource is available, the command is executed on
     * the latest resource.
     *
     * @param command The suspending command to execute, represented as a function that takes a resource.
     * @return The result of the execution.
     */
    public suspend fun <T> executeCoroutine(
        command: suspend (Resource) -> T
    ): T

    /**
     * Executes a command that returns a [Flow] of values, allowing reactive stream processing.
     *
     * If no resource is available, the command is queued for later execution.
     * The command can be executed more than once if the previous execution
     * has been cancelled due to detaching of a resource. In this case the command
     * is added again to the queue and starts waiting for the next resource.
     *
     * In case, if more than one resource is available, the command is executed on
     * each available resource, and all emitted items are combined into one resulting flow.
     *
     * @param command The command to execute, represented as a function that takes a resource and returns a [Flow].
     * @return A [Flow] of the result values.
     */
    public fun <T> executeFlow(
        command: (Resource) -> Flow<T>
    ): Flow<T>

    /**
     * Cancel all pending non-processed unit commands executed by [execute].
     */
    public fun cleanUp()

}
