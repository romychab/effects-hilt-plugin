package com.uandcode.effects.core.exceptions

import com.uandcode.effects.core.exceptions.AnnotationNames.effectApplication
import com.uandcode.effects.core.exceptions.AnnotationNames.effectClass
import kotlin.reflect.KClass

public class EffectNotFoundException(
    effect: KClass<*>
) : Exception(
    "Effect '${effect.simpleName}' not found.\n" +
    "1. Make sure you have added $effectClass annotation to the effect class implementation.\n" +
    "2. Make sure you have added $effectApplication annotation to your main application module.\n"
)
