package com.uandcode.effects.core.annotations

/**
 * This annotation should be used in the main application module. It is a
 * marker signalling that all dependencies from all modules are gathered into
 * the final application build. As a result, all effect proxies from all
 * modules can be safely generated.
 *
 * Usage example:
 *
 * ```
 * @EffectApplication
 * class MyApp : Application()
 * ```
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
public annotation class EffectApplication
