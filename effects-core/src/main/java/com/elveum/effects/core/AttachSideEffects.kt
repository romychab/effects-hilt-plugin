package com.elveum.effects.core

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.elveum.effects.core.actors.SideEffectImplementation
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

/**
 * Inject this class into your activity to make side-effects available for
 * the activity and its fragments.
 *
 * ```
 * @AndroidEntryPoint
 * class MyActivity : AppCompatActivity() {
 *     @Inject
 *     lateinit var sideEffects: AttachSideEffects
 * }
 * ```
 *
 * Lifecycle of all side-effect interfaces which are injected to your view-models
 * (both for activities and fragments) are tied to the Hilt `ActivityRetainedComponent`.
 * So they survive upon configuration changes. If you use fragments, then their view-models
 * receive the same instance of the side-effect interface within the activity. So
 * actually the following rules work here:
 * - 1 activity = 1 side-effect interface instance for all fragments
 * - side-effect interface lifecycle = activity view-model lifecycle
 *
 * Lifecycle of all side-effect implementations are tied to the Hilt `ActivityComponent`.
 * So side-effect implementations are created every time when activity is recreated. They
 * do not survive upon configuration changes.
 *
 * Moreover, all suspend methods of the side-effect implementation are executed in
 * the custom coroutine scope which automatically cancels suspend methods being
 * executed when the activity is going to be stopped. Afterwards, when
 * the activity restarts, all cancelled non-finished suspend methods
 * are re-executed again.
 */
@ActivityScoped
public class AttachSideEffects @Inject internal constructor(
    private val mediatorsManager: SideEffectMediatorsManager,
    private val activity: ComponentActivity,
    private val implementations: Set<@JvmSuppressWildcards SideEffectImplementation>
) {

    init {

        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                mediatorsManager.onStart(implementations)
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                mediatorsManager.onStop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                if (!activity.isChangingConfigurations) {
                    mediatorsManager.destroy()
                }
            }

        })

    }

}