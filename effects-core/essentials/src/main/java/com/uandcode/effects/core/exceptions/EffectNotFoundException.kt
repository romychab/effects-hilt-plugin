package com.uandcode.effects.core.exceptions

import com.uandcode.effects.stub.api.ProxyConfiguration
import kotlin.reflect.KClass

public class EffectNotFoundException(
    effect: KClass<*>,
    proxyConfiguration: ProxyConfiguration,
) : Exception(proxyConfiguration.effectNotFoundMessage(effect))
