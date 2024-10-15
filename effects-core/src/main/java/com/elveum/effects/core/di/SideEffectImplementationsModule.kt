package com.elveum.effects.core.di

import com.elveum.effects.annotations.SideEffect
import com.elveum.effects.core.actors.SideEffectImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.multibindings.ElementsIntoSet

/**
 * For internal usage.
 *
 * This module provides an empty stub set of all side-effect implementations.
 * The library generates modules for each class annotated with [SideEffect] and
 * those generated modules add implementations into the set provided by
 * [provideStubImplementationsSet] method.
 */
@Module
@InstallIn(ActivityComponent::class)
internal class SideEffectImplementationsModule {

    @Provides
    @ActivityScoped
    @ElementsIntoSet
    fun provideStubImplementationsSet(): Set<@JvmSuppressWildcards SideEffectImplementation> {
        return emptySet()
    }

}