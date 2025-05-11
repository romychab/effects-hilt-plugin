package com.uandcode.effects.koin.internal

internal class ConstructorArg : AutoCloseable {

    private var closeable: AutoCloseable? = null

    fun assignCloseable(closeable: AutoCloseable) {
        this.closeable = closeable
    }

    fun hasCloseable() = closeable != null

    override fun close() {
        closeable?.close()
        closeable = null
    }

}
