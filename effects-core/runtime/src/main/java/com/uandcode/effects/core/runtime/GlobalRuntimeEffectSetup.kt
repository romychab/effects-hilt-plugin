package com.uandcode.effects.core.runtime

import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.core.RootEffectScopes

/**
 * Make proxy implementations for all effect interfaces at runtime.
 *
 * Works only on JVM platforms. But on the other hand, you can drop
 * annotations and KSP from your project.
 *
 * Usage example:
 *
 * ```
 * class App : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         setupRuntimeEffectsGlobally()
 *     }
 * }
 * ```
 */
public fun setupRuntimeEffectsGlobally() {
    val runtimeGlobalScope = RootEffectScopes.empty.createChild(
        managedInterfaces = ManagedInterfaces.Everything,
        proxyEffectFactory = RuntimeProxyEffectFactory()
    )
    RootEffectScopes.setGlobal(runtimeGlobalScope)
}
