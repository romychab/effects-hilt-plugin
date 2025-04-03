package com.uandcode.effects.stub.api

import kotlin.reflect.KClass

/**
 * For internal usage.
 *
 * Defines the names of annotations which will appear in exceptions thrown
 * by the library. This configuration can be overridden by various
 * library extensions which can use different annotations.
 */
public interface ProxyConfiguration {

    public fun effectNotFoundMessage(clazz: KClass<*>): String

    public data class Default(
        val applicationAnnotationName: String = "@EffectApplication",
        val effectAnnotationName: String = "@EffectClass",
    ) : ProxyConfiguration {
        override fun effectNotFoundMessage(clazz: KClass<*>): String {
            return "Effect '${clazz.simpleName}' not found.\n" +
                    "1. Have you added $effectAnnotationName annotation to your effect class?\n" +
                    "2. Do not forget to add $applicationAnnotationName annotation to your application class.\n" +
                    "3. Review your DI framework configuration and make sure you have configured both DI and KSP plugin properly.\n" +
                    "4. If you write an integration test, make sure you have replaced RootEffectComponents properly."
        }

    }
}
