package com.elveum.effects.core.di

import com.elveum.effects.core.actors.MviPair
import com.elveum.effects.core.actors.MviEffectMediator
import com.elveum.effects.core.retain.DefaultRetainedData
import com.elveum.effects.core.retain.RetainedData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.multibindings.ElementsIntoSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import javax.inject.Qualifier

/**
 * For internal usage.
 *
 * Determines the lifecycle of the CoroutineScope used for mediators.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class MviEffectsMediatorScope

@Module
@InstallIn(ActivityRetainedComponent::class)
internal object MviEffectsMediatorsModule {

    @ActivityRetainedScoped
    @MviEffectsMediatorScope
    @Provides
    fun provideScope(): CoroutineScope {
        return MainScope()
    }

    @Provides
    @ElementsIntoSet
    @ActivityRetainedScoped
    fun provideStubMediatorsSet(): Set<@JvmSuppressWildcards MviPair> {
        return emptySet()
    }

    @Provides
    @ActivityRetainedScoped
    fun provideStubMediatorsMap(
        set: Set<@JvmSuppressWildcards MviPair>
    ): Map<String, @JvmSuppressWildcards MviEffectMediator<Any>> {
        val map = LinkedHashMap<String, MviEffectMediator<Any>>()
        set.forEach {
            map[it.interfaceName] = it.mediator
        }
        return map
    }

    @Provides
    @ActivityRetainedScoped
    fun provideRetainedData(): RetainedData {
        return DefaultRetainedData()
    }

}