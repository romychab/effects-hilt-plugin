package com.uandcode.effects.core.exceptions

import com.uandcode.effects.stub.api.ProxyConfiguration
import kotlin.reflect.KClass

public class EffectNotFoundException(
    effect: KClass<*>,
    proxyConfiguration: ProxyConfiguration,
) : Exception("Effect '${effect.simpleName}' not found.\n" +
        "1. Have you added ${proxyConfiguration.effectAnnotationName} annotation to your effect class?\n" +
        "2. Do not forget to add ${proxyConfiguration.applicationAnnotationName} annotation to your application class.\n" +
        "3. Review your DI framework configuration and make sure you have configured both DI and KSP plugin properly.\n" +
        "4. If you write an integration test, make sure you have replaced RootEffectComponents properly.")
