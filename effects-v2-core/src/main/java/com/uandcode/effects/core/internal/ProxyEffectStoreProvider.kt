package com.uandcode.effects.core.internal

import com.uandcode.effects.stub.GeneratedProxyEffectStore
import com.uandcode.effects.stub.api.InvalidEffectSetupException
import com.uandcode.effects.stub.api.ProxyEffectStore

internal object ProxyEffectStoreProvider {

    fun getGeneratedProxyEffectStore(): ProxyEffectStore {
        try {
            return GeneratedProxyEffectStore
        } catch (e: Throwable) {
            throw InvalidEffectSetupException()
        }
    }

}
