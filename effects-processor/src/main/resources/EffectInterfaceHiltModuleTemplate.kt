%PACKAGE_STATEMENT%

import com.elveum.effects.core.CommandExecutor
import com.elveum.effects.core.EffectController
import com.elveum.effects.core.EffectRecord
import com.elveum.effects.core.HiltOverridable
import com.elveum.effects.core.ObservableResourceStore
import com.elveum.effects.core.getInstanceWithMaxPriority
import com.elveum.effects.core.impl.CommandExecutorImpl
import com.elveum.effects.core.impl.EffectControllerImpl
import com.elveum.effects.core.impl.ObservableResourceStoreImpl
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
    @IntoSet
    fun provideMediatorIntoSet(commandExecutor: CommandExecutor<%TARGET_INTERFACE_NAME%>): HiltOverridable<%TARGET_INTERFACE_NAME%> {
        val mediator = %MEDIATOR_NAME%(commandExecutor)
        return HiltOverridable(mediator, HiltOverridable.%PRIORITY_CONST%)
    }

    @Provides
    fun provideMediator(set: Set<@JvmSuppressWildcards HiltOverridable<%TARGET_INTERFACE_NAME%>>): %TARGET_INTERFACE_NAME% {
        return set.getInstanceWithMaxPriority()
    }

    @Provides
    fun provideControllerOfEffectInterface(
        observableResourceStore: ObservableResourceStore<%TARGET_INTERFACE_NAME%>
    ): EffectController<%TARGET_INTERFACE_NAME%> {
        return EffectControllerImpl(observableResourceStore)
    }

}
