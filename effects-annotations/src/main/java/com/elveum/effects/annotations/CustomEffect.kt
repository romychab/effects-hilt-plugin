package com.elveum.effects.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
public annotation class CustomEffect(
    public val target: KClass<*> = Any::class,
    public val installIn: KClass<*> = Any::class,
)
