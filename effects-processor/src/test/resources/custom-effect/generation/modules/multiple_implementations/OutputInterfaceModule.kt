import com.elveum.effects.core.CommandExecutor
import com.elveum.effects.core.EffectController
import com.elveum.effects.core.EffectRecord
import com.elveum.effects.core.ObservableResourceStore
import com.elveum.effects.core.impl.CommandExecutorImpl
import com.elveum.effects.core.impl.EffectControllerImpl
import com.elveum.effects.core.impl.ObservableResourceStoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.multibindings.IntoSet
import javax.inject.Provider
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestInterfaceEffectModule {

    @Provides
    @Singleton
    fun provideStore(): ObservableResourceStore<TestInterface> {
        return ObservableResourceStoreImpl()
    }

    @Provides
    fun provideCommandExecutor(resourceStore: ObservableResourceStore<TestInterface>): CommandExecutor<TestInterface> {
        return CommandExecutorImpl(resourceStore)
    }

    @Provides
    fun provideMediator(commandExecutor: CommandExecutor<TestInterface>): TestInterface {
        return __TestInterfaceMediator(commandExecutor)
    }

    @Provides
    fun provideControllerOfEffectInterface(
        observableResourceStore: ObservableResourceStore<TestInterface>
    ): EffectController<TestInterface> {
        return EffectControllerImpl(observableResourceStore)
    }

}
