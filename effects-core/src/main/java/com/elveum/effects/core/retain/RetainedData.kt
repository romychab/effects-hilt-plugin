package com.elveum.effects.core.retain

/**
 * Inject this interface to your side-effect implementation.
 * Any values saved to this [RetainedData] will survive after screen
 * rotation.
 *
 * Note: but they will not survive upon process killing.
 */
interface RetainedData {

    operator fun <T : Any> get(key: String): T?

    operator fun <T : Any> set(key: String, value: T)

}