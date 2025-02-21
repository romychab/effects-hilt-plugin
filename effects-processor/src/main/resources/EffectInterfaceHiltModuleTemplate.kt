%PACKAGE_STATEMENT%

import com.elveum.effects.core.v2.CommandExecutor
import com.elveum.effects.core.v2.EffectCleaner
import com.elveum.effects.core.v2.EffectController
import com.elveum.effects.core.v2.EffectRecord
import com.elveum.effects.core.v2.ObservableResourceStore
import com.elveum.effects.core.v2.impl.CommandExecutorImpl
import com.elveum.effects.core.v2.impl.EffectCleanerImpl
import com.elveum.effects.core.v2.impl.EffectControllerImpl
import com.elveum.effects.core.v2.impl.ObservableResourceStoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.multibindings.IntoSet
import javax.inject.Provider
import %HILT_COMPONENT%
import %HILT_SCOPE%

@Module
@InstallIn(%HILT_COMPONENT_NAME%::class)
object %CLASSNAME% {

    @Provides
    @%HILT_SCOPE_NAME%
    fun provideStore(): ObservableResourceStore<%TARGET_INTERFACE_NAME%> {
        return ObservableResourceStoreImpl()
    }

    @Provides
    fun provideCommandExecutor(resourceStore: ObservableResourceStore<%TARGET_INTERFACE_NAME%>): CommandExecutor<%TARGET_INTERFACE_NAME%> {
        return CommandExecutorImpl(resourceStore)
    }

    @Provides
    fun provideMediator(commandExecutor: CommandExecutor<%TARGET_INTERFACE_NAME%>): %TARGET_INTERFACE_NAME% {
        return %MEDIATOR_NAME%(commandExecutor)
    }

    @Provides
    fun provideControllerOfEffectInterface(
        observableResourceStore: ObservableResourceStore<%TARGET_INTERFACE_NAME%>
    ): EffectController<%TARGET_INTERFACE_NAME%> {
        return EffectControllerImpl(observableResourceStore)
    }

    @Provides
    fun provideCleanerOfEffectInterface(
        observableResourceStore: ObservableResourceStore<%TARGET_INTERFACE_NAME%>,
    ): EffectCleaner<%TARGET_INTERFACE_NAME%> {
        return EffectCleanerImpl(observableResourceStore)
    }

}
