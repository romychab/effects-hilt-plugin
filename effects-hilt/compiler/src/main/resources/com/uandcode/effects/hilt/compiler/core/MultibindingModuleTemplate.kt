@file:OptIn(com.uandcode.effects.hilt.InternalEffectsHiltApi::class)

%PACKAGE_STATEMENT%

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.hilt.internal.InternalRegisteredEffect
import com.uandcode.effects.hilt.internal.qualifiers.AbstractInternalQualifier
import com.uandcode.effects.hilt.internal.qualifiers.getEffectScopeWithMaxPriority
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet

@Module
@InstallIn(SingletonComponent::class)
public object %CLASSNAME% {

    @Provides
    @ElementsIntoSet
    public fun emptyRegisteredEffectSet(): Set<@JvmSuppressWildcards InternalRegisteredEffect> {
        return emptySet()
    }

    @Provides
    @ElementsIntoSet
    public fun emptyQualifierSet(): Set<@JvmSuppressWildcards AbstractInternalQualifier> {
        return emptySet()
    }

    @Provides
    public fun provideEffectScope(
        qualifiers: Set<@JvmSuppressWildcards AbstractInternalQualifier>
    ): EffectScope {
        return qualifiers.getEffectScopeWithMaxPriority()
    }

}
