package com.elveum.effects.core.di

import com.elveum.effects.annotations.MviEffect
import com.elveum.effects.core.actors.MviEffectImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.multibindings.ElementsIntoSet

/**
 * For internal usage.
 *
 * This module provides an empty stub set of all MVI-effect implementations.
 * The library generates modules for each class annotated with [MviEffect] and
 * those generated modules add implementations into the set provided by
 * [provideStubImplementationsSet] method.
 */
@Module
@InstallIn(ActivityComponent::class)
internal object MviEffectImplementationsModule {

    @Provides
    @ActivityScoped
    @ElementsIntoSet
    fun provideStubImplementationsSet(): Set<@JvmSuppressWildcards MviEffectImplementation> {
        return emptySet()
    }

}