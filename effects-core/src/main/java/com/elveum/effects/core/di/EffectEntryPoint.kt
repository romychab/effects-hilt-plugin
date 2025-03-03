package com.elveum.effects.core.di

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.elveum.effects.core.EffectStore
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
public interface ActivityEffectEntryPoint {
    public fun getEffectStore(): EffectStore
}


@EntryPoint
@InstallIn(FragmentComponent::class)
public interface FragmentEffectEntryPoint {
    public fun getEffectStore(): EffectStore
}

public fun ComponentActivity.getEffectEntryPoint(): ActivityEffectEntryPoint {
    return EntryPointAccessors.fromActivity<ActivityEffectEntryPoint>(this)
}

public fun Fragment.getEffectEntryPoint(): FragmentEffectEntryPoint {
    return EntryPointAccessors.fromFragment<FragmentEffectEntryPoint>(this)
}
