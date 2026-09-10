@file:OptIn(com.uandcode.effects.hilt.InternalEffectsHiltApi::class)

%PACKAGE_STATEMENT%

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
public object %CLASSNAME% {

    @Provides
    @FragmentScoped
    public fun provideQualifier(
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
    public fun provideQualifierToSet(
        qualifier: FragmentQualifier
    ): AbstractInternalQualifier = qualifier

}
