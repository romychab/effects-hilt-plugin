package com.uandcode.effects.hilt.internal.modules

import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.hilt.internal.InternalRegisteredEffect
import com.uandcode.effects.hilt.internal.filterByQualifier
import com.uandcode.effects.hilt.internal.qualifiers.AbstractInternalQualifier
import com.uandcode.effects.hilt.internal.qualifiers.ActivityRetainedQualifier
import com.uandcode.effects.hilt.internal.qualifiers.SingletonQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
internal object ActivityRetainedEffectModule {

    @Provides
    @ActivityRetainedScoped
    fun provideQualifier(
        parentQualifier: SingletonQualifier,
        registeredEffects: Set<@JvmSuppressWildcards InternalRegisteredEffect>
    ): ActivityRetainedQualifier {
        return ActivityRetainedQualifier(
            parentQualifier.scope.createChild(
                ManagedInterfaces.ListOf(
                    *registeredEffects.filterByQualifier(ActivityRetainedQualifier::class)
                )
            )
        )
    }

    @Provides
    @IntoSet
    fun provideQualifierToSet(
        qualifier: ActivityRetainedQualifier
    ): AbstractInternalQualifier = qualifier

}
