@file:OptIn(com.uandcode.effects.hilt.InternalEffectsHiltApi::class)

%PACKAGE_STATEMENT%

import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.core.RootEffectScopes
import com.uandcode.effects.hilt.internal.InternalRegisteredEffect
import com.uandcode.effects.hilt.internal.filterByQualifier
import com.uandcode.effects.hilt.internal.qualifiers.AbstractInternalQualifier
import com.uandcode.effects.hilt.internal.qualifiers.SingletonQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
public object %CLASSNAME% {

    @Provides
    @Singleton
    public fun provideQualifier(
        registeredEffects: Set<@JvmSuppressWildcards InternalRegisteredEffect>
    ): SingletonQualifier {
        return SingletonQualifier(
            RootEffectScopes.empty.createChild(
                ManagedInterfaces.ListOf(
                    *registeredEffects.filterByQualifier(SingletonQualifier::class)
                )
            )
        )
    }

    @Provides
    @IntoSet
    public fun provideQualifierToSet(
        qualifier: SingletonQualifier
    ): AbstractInternalQualifier = qualifier

}
