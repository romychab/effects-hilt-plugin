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
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
object TestInterfaceEffectModule {

    @Provides
    @FragmentScoped
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

    @Provides
    fun provideCleanerOfEffectInterface(
        observableResourceStore: ObservableResourceStore<TestInterface>,
    ): EffectCleaner<TestInterface> {
        return EffectCleanerImpl(observableResourceStore)
    }

}
