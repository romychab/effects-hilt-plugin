package com.elveum.effects.core

import androidx.activity.ComponentActivity
import com.elveum.effects.core.actors.SideEffectImplementation
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
internal class EffectsLifecycleControllerImpl @Inject constructor(
    private val mediatorsManager: SideEffectMediatorsManager,
    private val activity: ComponentActivity,
    private val implementations: Set<@JvmSuppressWildcards SideEffectImplementation>
) : EffectsLifecycleController {

    override fun startEffects() {
        mediatorsManager.onStart(implementations)
    }

    override fun stopEffects() {
        mediatorsManager.onStop()
    }

    override fun destroyEffects() {
        if (!activity.isChangingConfigurations) {
            mediatorsManager.destroy()
        }
    }
}