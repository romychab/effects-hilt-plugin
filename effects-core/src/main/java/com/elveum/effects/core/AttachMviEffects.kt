package com.elveum.effects.core

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

/**
 * Inject this class into your activity to make MVI-effects available for
 * the activity and its fragments.
 *
 * ```
 * @AndroidEntryPoint
 * class MyActivity : AppCompatActivity() {
 *     @Inject
 *     lateinit var mviEffects: AttachMviEffects
 * }
 * ```
 *
 * Lifecycle of all MVI-effect interfaces which are injected to your view-models
 * (both for activities and fragments) are tied to the Hilt `ActivityRetainedComponent`.
 * So they survive upon configuration changes. If you use fragments, then their view-models
 * receive the same instance of the MVI-effect interface within the activity. So
 * actually the following rules work here:
 * - 1 activity = 1 MVI-effect interface instance for all fragments
 * - MVI-effect interface lifecycle = activity view-model lifecycle
 *
 * Lifecycle of all MVI-effect implementations are tied to the Hilt `ActivityComponent`.
 * So MVI-effect implementations are created every time when activity is recreated. They
 * do not survive upon configuration changes.
 *
 * Moreover, all suspend methods of the MVI-effect implementation are executed in
 * the custom coroutine scope which automatically cancels suspend methods being
 * executed when the activity is going to be stopped. Afterwards, when
 * the activity restarts, all cancelled non-finished suspend methods
 * are re-executed again.
 */
@ActivityScoped
public class AttachMviEffects @Inject internal constructor(
    private val mviEffectsLifecycleController: MviEffectsLifecycleController,
    activity: ComponentActivity,
) {

    init {

        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                mviEffectsLifecycleController.startEffects()
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                mviEffectsLifecycleController.stopEffects()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                mviEffectsLifecycleController.destroyEffects()
            }
        })
    }

}

@Deprecated(
    message = "Use AttachMviEffects instead.",
    replaceWith = ReplaceWith("AttachMviEffects"),
)
public typealias AttachSideEffects = AttachMviEffects