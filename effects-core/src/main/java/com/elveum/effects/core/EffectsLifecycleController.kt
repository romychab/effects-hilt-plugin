package com.elveum.effects.core

/**
 * Manage effects lifecycle.
 *
 * Usually you don't need to use this class directly.
 */
public interface EffectsLifecycleController {
    /**
     * Start effects. All effects called from view-models will be delivered
     * to activity after this call.
     */
    public fun startEffects()

    /**
     * Stop effects. All effects called from view-models will be postponed
     * until [startEffects] is called.
     */
    public fun stopEffects()

    /**
     * Destroy all postponed effects if activity is going to be completely destroyed.
     */
    public fun destroyEffects()
}