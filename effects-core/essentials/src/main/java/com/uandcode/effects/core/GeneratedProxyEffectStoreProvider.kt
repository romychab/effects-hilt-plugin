package com.uandcode.effects.core

import com.uandcode.effects.core.kspcontract.AnnotationBasedProxyEffectStore
import com.uandcode.effects.stub.api.InvalidEffectSetupException
import com.uandcode.effects.stub.api.ProxyEffectStore

public object GeneratedProxyEffectStoreProvider {

    public fun getGeneratedProxyEffectStore(): ProxyEffectStore {
        try {
            return AnnotationBasedProxyEffectStore
        } catch (e: Throwable) {
            throw InvalidEffectSetupException()
        }
    }

}
