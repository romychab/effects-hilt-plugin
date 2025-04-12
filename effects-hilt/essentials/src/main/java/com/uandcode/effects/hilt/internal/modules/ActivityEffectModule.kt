package com.uandcode.effects.hilt.internal.modules

import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.hilt.internal.InternalRegisteredEffect
import com.uandcode.effects.hilt.internal.filterByQualifier
import com.uandcode.effects.hilt.internal.qualifiers.AbstractInternalQualifier
import com.uandcode.effects.hilt.internal.qualifiers.ActivityQualifier
import com.uandcode.effects.hilt.internal.qualifiers.ActivityRetainedQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityComponent::class)
internal object ActivityEffectModule {

    @Provides
    @ActivityScoped
    fun provideQualifier(
        parentQualifier: ActivityRetainedQualifier,
        registeredEffects: Set<@JvmSuppressWildcards InternalRegisteredEffect>
    ): ActivityQualifier {
        return ActivityQualifier(
            parentQualifier.scope.createChild(
                ManagedInterfaces.ListOf(
                    *registeredEffects.filterByQualifier(ActivityQualifier::class)
                )
            )
        )
    }

    @Provides
    @IntoSet
    fun provideQualifierToSet(
        qualifier: ActivityQualifier
    ): AbstractInternalQualifier = qualifier

}
