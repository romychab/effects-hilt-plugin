package com.uandcode.effects.core

/**
 * A simple DI container which provides an auto-generated instance of [Effect]
 * interface that can be used for interaction between components with different
 * lifecycle.
 *
 * Component with longer lifecycle must use an [Effect] instance created
 * by [provide] method.
 *
 * Component with shorter lifecycle must manually attach and detach an
 * implementation of [Effect] interface by using controllers returned by
 * either [createController] or [createBoundController] methods.
 */
public interface EffectModule<Effect : Any> {

    /**
     * Provide a mediator instance of [Effect] interface. This instance
     * can be safely used in components with longer lifecycle. For example,
     * you can inject it to ViewModel instances, singletons, etc.
     */
    public fun provide(): Effect

    /**
     * Create a controller which can attach a real [Effect] implementation
     * to the mediators returned by [provide].
     *
     * When an implementation of [Effect] is attached via the returned controller,
     * then all pending calls executed on a mediator returned by [provide]
     * are delegated to the attached implementation.
     *
     * @see EffectController
     */
    public fun <T : Effect> createController(): EffectController<T>

    /**
     * The same as [createController], but with pre-defined
     * [Effect] implementation specified in [boundImplementationProvider] argument.
     *
     * @see createController
     * @see BoundEffectController
     * @see EffectController
     */
    public fun <T : Effect> createBoundController(
        boundImplementationProvider: () -> T
    ): BoundEffectController<T>

    /**
     * Cancel all pending non-finished calls executed on mediators
     * returned by [provide] method.
     */
    public fun cleanUp()

}
