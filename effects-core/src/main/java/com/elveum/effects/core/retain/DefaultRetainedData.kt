package com.elveum.effects.core.retain

class DefaultRetainedData : RetainedData {

    private val dataMap = mutableMapOf<String, Any>()

    override fun <T : Any> get(key: String): T? {
        return dataMap[key] as? T
    }

    override fun <T : Any> set(key: String, value: T) {
        dataMap[key] = value
    }

}