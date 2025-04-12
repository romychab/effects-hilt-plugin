%PACKAGE_STATEMENT%

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.getController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import %HILT_COMPONENT%

@Module
@InstallIn(%HILT_COMPONENT_NAME%::class)
internal object %CLASSNAME% {
    @Provides
    fun provideController(effectScope: EffectScope): EffectController<%EFFECT_NAME%> {
        return effectScope.getController()
    }
}