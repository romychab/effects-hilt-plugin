package com.elveum.effects.core

/**
 * Usually you don't need to use this interface directly.
 * See [lazyEffect] delegate instead of 'EffectProvider' composable function.
 *
 * This interface allows you manual managing a lifecycle of an effect implementation
 * of type [EffectImplementation].
 *
 * You can use this interface for attaching any effect implementation
 * to the target interface.
 *
 * Effect controllers can be created by using Hilt/Dagger Injection:
 *
 * ```
 * @Inject
 * lateinit var myEffectController: EffectController<MyEffectImpl>
 *
 * override fun onStart() {
 *     super.onStart()
 *     myEffectController.start(MyEffectImpl())
 * }
 * ```
 *
 */
public interface EffectController<EffectImplementation> {

    /**
     * The current effect implementation attached to the effect interface if available.
     */
    public val effectImplementation: EffectImplementation?

    /**
     * Attach the specified [effectImplementation] to the target interface,
     * so any method executed on the interface will be delegated to the
     * [effectImplementation] instance.
     *
     * If this controller has already attached effect implementation, nothing happens.
     */
    public fun start(effectImplementation: EffectImplementation)

    /**
     * Detach the previously attached [effectImplementation] from the target
     * interface.
     */
    public fun stop()

    /**
     * Whether an effect implementation is attached or not at this moment.
     */
    public val isStarted: Boolean get() = effectImplementation != null

}
