package com.uandcode.effects.koin

import com.uandcode.effects.koin.annotations.KoinEffect
import com.uandcode.effects.koin.internal.overrideInstances
import com.uandcode.effects.koin.internal.installRootEffects
import com.uandcode.effects.koin.internal.registerEffectScopeValidator
import com.uandcode.effects.koin.kspcontract.AnnotationBasedKoinEffectExtension.installGeneratedEffects
import org.koin.core.KoinApplication

/**
 * Install all effects annotated with [KoinEffect] annotation
 * to the [KoinApplication].
 *
 * Requirements:
 * - This function requires the KSP annotation processor to be used.
 * - This function must be called at the end of [KoinApplication]
 * definition. For example:
 *
 * ```
 * startKoin {
 *     androidContext(applicationContext)
 *     androidLogger()
 *     modules(myModule1, myModule2)
 *
 *     installAnnotatedEffects() // <-- the last instruction
 * }
 * ```
 */
public fun KoinApplication.installAnnotatedKoinEffects() {
    installGeneratedEffects(this)
    overrideInstances()
    registerEffectScopeValidator()
}

/**
 * Install effects resolved at runtime. Use this function only if
 * you don't want to use annotations and KSP. Otherwise, see [installAnnotatedKoinEffects].
 *
 * Requirements:
 * - the function must be called at the end of [KoinApplication] definition.
 * - all effect interfaces must be registered manually in your own modules
 *   by using `effect` function
 * - if you want to scope an effect, use `effectScope` instead of `scope`.
 * - the `effectScope` call for each scope qualifier must be declared only
 *   once per Koin Application
 *
 * ```
 * // effect interface
 * interface Router
 *
 * // effect implementation
 * class NavComponentRouter : Router { ... }
 *
 * val myModule = module {
 *     effect<Router>() // example of unscoped global effect
 *     effectScope<MyScopeType> {
 *         effect<Router>() // example of scoped effect
 *     }
 * }
 * ```
 *
 * Example of Koin Application definition:
 *
 * ```
 * startKoin {
 *     androidContext(applicationContext)
 *     androidLogger()
 *     modules(myModule1, myModule2)
 *
 *     installRuntimeKoinEffects() // <-- the last instruction
 * }
 * ```
 */
public fun KoinApplication.installRuntimeKoinEffects() {
    installRootEffects()
    overrideInstances()
    registerEffectScopeValidator()
}

