%PACKAGE_STATEMENT%

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.getController
import com.uandcode.effects.core.getProxy
import com.uandcode.effects.hilt.internal.InternalRegisteredEffect
import com.uandcode.effects.hilt.internal.qualifiers.AbstractInternalQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.multibindings.IntoSet
import %HILT_COMPONENT%

@Module
@InstallIn(%HILT_COMPONENT_NAME%::class)
internal object %CLASSNAME% {

    @Provides
    @IntoSet
    fun registerEffect(): InternalRegisteredEffect {
        return InternalRegisteredEffect(%TARGET_INTERFACE_NAME%::class, AbstractInternalQualifier.%QUALIFIER%)
    }

    @Provides
    fun provideEffect(effectScope: EffectScope): %TARGET_INTERFACE_NAME% {
        return effectScope.getProxy()
    }

    @Provides
    fun provideController(effectScope: EffectScope): EffectController<%TARGET_INTERFACE_NAME%> {
        return effectScope.getController()
    }

}
