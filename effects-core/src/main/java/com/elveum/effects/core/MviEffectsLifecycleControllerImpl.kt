package com.elveum.effects.core

import androidx.activity.ComponentActivity
import com.elveum.effects.core.actors.MviEffectImplementation
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
internal class MviEffectsLifecycleControllerImpl @Inject constructor(
    private val mediatorsManager: MviEffectMediatorsManager,
    private val activity: ComponentActivity,
    private val implementations: Set<@JvmSuppressWildcards MviEffectImplementation>
) : MviEffectsLifecycleController {

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