package com.uandcode.effects.hilt.internal.modules

import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.hilt.internal.InternalRegisteredEffect
import com.uandcode.effects.hilt.internal.filterByQualifier
import com.uandcode.effects.hilt.internal.qualifiers.AbstractInternalQualifier
import com.uandcode.effects.hilt.internal.qualifiers.ActivityQualifier
import com.uandcode.effects.hilt.internal.qualifiers.FragmentQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import dagger.multibindings.IntoSet

@Module
@InstallIn(FragmentComponent::class)
internal object FragmentEffectModule {

    @Provides
    @FragmentScoped
    fun provideQualifier(
        parentQualifier: ActivityQualifier,
        registeredEffects: Set<@JvmSuppressWildcards InternalRegisteredEffect>
    ): FragmentQualifier {
        return FragmentQualifier(
            parentQualifier.scope.createChild(
                ManagedInterfaces.ListOf(
                    *registeredEffects.filterByQualifier(FragmentQualifier::class)
                )
            )
        )
    }

    @Provides
    @IntoSet
    fun provideQualifierToSet(
        qualifier: FragmentQualifier
    ): AbstractInternalQualifier = qualifier

}
