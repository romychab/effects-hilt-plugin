package com.uandcode.effects.hilt.internal.modules

import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.hilt.internal.HiltViewModelEffectScope
import com.uandcode.effects.hilt.internal.InternalRegisteredEffect
import com.uandcode.effects.hilt.internal.filterByQualifier
import com.uandcode.effects.hilt.internal.qualifiers.AbstractInternalQualifier
import com.uandcode.effects.hilt.internal.qualifiers.ActivityRetainedQualifier
import com.uandcode.effects.hilt.internal.qualifiers.ViewModelQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.ViewModelLifecycle
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.multibindings.IntoSet

@Module
@InstallIn(ViewModelComponent::class)
internal object ViewModelEffectModule {

    @Provides
    @ViewModelScoped
    fun provideQualifier(
        parentQualifier: ActivityRetainedQualifier,
        viewModelLifecycle: ViewModelLifecycle,
        registeredEffects: Set<@JvmSuppressWildcards InternalRegisteredEffect>
    ): ViewModelQualifier {
        val component = parentQualifier.scope.createChild(
            ManagedInterfaces.ListOf(
                *registeredEffects.filterByQualifier(ViewModelQualifier::class)
            )
        )
        return ViewModelQualifier(
            HiltViewModelEffectScope(component, viewModelLifecycle)
        )
    }

    @Provides
    @IntoSet
    fun provideQualifierToSet(
        qualifier: ViewModelQualifier
    ): AbstractInternalQualifier = qualifier

}
