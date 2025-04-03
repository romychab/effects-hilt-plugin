package com.uandcode.effects.core.runtime

import com.uandcode.effects.stub.api.ProxyConfiguration
import kotlin.reflect.KClass

internal object RuntimeProxyConfiguration : ProxyConfiguration {
    override fun effectNotFoundMessage(clazz: KClass<*>): String {
        return "Effect not found: ${clazz.qualifiedName}. " +
            "You are using the runtime effect component, which means that for each interface " +
            "defined in the component, a proxy implementation is created through reflection API at runtime."
    }
}
