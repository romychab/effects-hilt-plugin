package com.elveum.effects.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Deprecated(
    message = "This annotation will be removed in the future releases because it has the same name as SideEffect { ... } component in Jetpack Compose. Use MviEffect instead.",
    replaceWith = ReplaceWith("MviEffect")
)
public annotation class SideEffect(
    public val target: KClass<*> = Any::class,
)
