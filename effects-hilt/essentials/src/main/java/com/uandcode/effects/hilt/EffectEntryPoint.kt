package com.uandcode.effects.hilt

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.uandcode.effects.core.EffectScope
import dagger.hilt.android.EntryPointAccessors

/**
 * Implemented by an entry point generated in the application module.
 *
 * The generated interface carries the `@EntryPoint` and `@InstallIn`
 * annotations, so the Hilt component implements this one too and the cast
 * performed by [EntryPointAccessors] succeeds.
 */
public interface ActivityEffectEntryPoint {
    public fun getEffectScope(): EffectScope
}

/** @see ActivityEffectEntryPoint */
public interface FragmentEffectEntryPoint {
    public fun getEffectScope(): EffectScope
}

public fun ComponentActivity.getEffectEntryPoint(): ActivityEffectEntryPoint {
    return EntryPointAccessors.fromActivity<ActivityEffectEntryPoint>(this)
}

public fun Fragment.getEffectEntryPoint(): FragmentEffectEntryPoint {
    return EntryPointAccessors.fromFragment<FragmentEffectEntryPoint>(this)
}
