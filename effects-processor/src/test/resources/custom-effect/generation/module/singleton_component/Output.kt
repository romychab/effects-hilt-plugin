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
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestInterfaceModule {

    @Provides
    @Singleton
    fun provideStore(): ObservableResourceStore<TestClass> {
        return ObservableResourceStoreImpl()
    }

    @Provides
    fun provideCommandExecutor(resourceStore: ObservableResourceStore<TestClass>): CommandExecutor<TestClass> {
        return CommandExecutorImpl(resourceStore)
    }

    @Provides
    fun provideMediator(commandExecutor: CommandExecutor<TestClass>): TestInterface {
        return TestInterfaceMediator(commandExecutor)
    }

    @Provides
    fun provideImplController(
        observableResourceStore: ObservableResourceStore<TestClass>
    ): EffectController<TestClass> {
        return EffectControllerImpl(observableResourceStore)
    }

    @Provides
    fun provideController(
        controller: EffectController<TestClass>
    ): EffectController<TestInterface> {
        @Suppress("UNCHECKED_CAST")
        return controller as EffectController<TestInterface>
    }

    @Provides
    fun provideCleaner(
        cleaner: EffectCleaner<TestClass>
    ): EffectCleaner<TestInterface> {
        @Suppress("UNCHECKED_CAST")
        return cleaner as EffectCleaner<TestInterface>
    }

    @Provides
    fun provideImplCleaner(
        observableResourceStore: ObservableResourceStore<TestClass>,
    ): EffectCleaner<TestClass> {
        return EffectCleanerImpl(observableResourceStore)
    }

    @Provides
    @IntoSet
    fun provideEffectRecord(
        controllerProvider: Provider<EffectController<TestClass>>,
    ): EffectRecord {
        return EffectRecord(
            effectImplementationClass = TestClass::class,
            effectInterfaceClass = TestInterface::class,
            controllerProvider = { controllerProvider.get() },
        )
    }

}
