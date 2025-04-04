package com.uandcode.effects.core.mocks

import com.uandcode.effects.core.mocks.Effect.Companion.expectedResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow

public interface Effect {

    public fun unitRun(input: String)

    public suspend fun coroutineRun(
        input: String,
        emitDelay: Long = Effect.EMIT_DELAY,
    ): String

    public fun flowRun(): Flow<String>

    public companion object {

        public const val EMIT_DELAY: Long = 100L

        public fun expectedResult(input: String, resultModifier: String = ""): String {
            return listOf("result", input, resultModifier).joinToString()
        }

    }
}

public class EffectImpl(private val resultModifier: String = "") : Effect {

    private val channel = Channel<String>(
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        capacity = 1,
    )

    override fun unitRun(input: String): Unit = Unit

    override suspend fun coroutineRun(input: String, emitDelay: Long): String {
        delay(if (emitDelay < 0) Effect.EMIT_DELAY else emitDelay)
        return expectedResult(input, resultModifier)
    }

    override fun flowRun(): Flow<String> {
        return channel.receiveAsFlow().map {
            expectedResult(it, resultModifier)
        }
    }

    public fun send(value: String) {
        channel.trySend(value)
    }

    public fun fail(exception: Exception) {
        channel.close(exception)
    }

    public fun complete() {
        channel.close()
    }

}
