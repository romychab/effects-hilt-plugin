%PACKAGE_STATEMENT%

import com.elveum.effects.core.v2.EffectCleaner
import com.elveum.effects.core.v2.EffectController
import com.elveum.effects.core.v2.EffectRecord
import com.elveum.effects.core.v2.ObservableResourceStore
import com.elveum.effects.core.v2.impl.EffectCleanerImpl
import com.elveum.effects.core.v2.impl.EffectControllerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.multibindings.IntoSet
import javax.inject.Provider
import %TARGET_INTERFACE_CLASSNAME%
import %HILT_COMPONENT%

@Module
@InstallIn(%HILT_COMPONENT_NAME%::class)
object %CLASSNAME% {

    @Provides
    fun provideCleanerOfEffectImpl(
        observableResourceStore: ObservableResourceStore<%TARGET_INTERFACE_NAME%>,
    ): EffectCleaner<%EFFECT_IMPL_NAME%> {
        return EffectCleanerImpl(observableResourceStore)
    }

    @Provides
    fun provideControllerOfEffectImpl(
        observableResourceStore: ObservableResourceStore<%TARGET_INTERFACE_NAME%>
    ): EffectController<%EFFECT_IMPL_NAME%> {
        return EffectControllerImpl(observableResourceStore)
    }

    @Provides
    @IntoSet
    fun provideEffectRecord(
        controllerProvider: Provider<EffectController<%TARGET_INTERFACE_NAME%>>,
    ): EffectRecord {
        return EffectRecord(
            effectImplementationClass = %EFFECT_IMPL_NAME%::class,
            effectInterfaceClass = %TARGET_INTERFACE_NAME%::class,
            controllerProvider = { controllerProvider.get() },
        )
    }

}