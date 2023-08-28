package com.elveum.effects.core.retain

class WrappedRetainedData(
    private val origin: RetainedData,
    private val keyPrefix: String
) : RetainedData {

    override fun <T : Any> get(key: String): T? {
        return origin["$keyPrefix::$key"]
    }

    override fun <T : Any> set(key: String, value: T) {
        origin["$keyPrefix::$key"] = value
    }

}