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
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TestClass1ImplModule {

    @Provides
    fun provideCleanerOfEffectImpl(
        observableResourceStore: ObservableResourceStore<TestInterface>,
    ): EffectCleaner<TestClass1> {
        return EffectCleanerImpl(observableResourceStore)
    }

    @Provides
    fun provideControllerOfEffectImpl(
        observableResourceStore: ObservableResourceStore<TestInterface>
    ): EffectController<TestClass1> {
        return EffectControllerImpl(observableResourceStore)
    }

    @Provides
    @IntoSet
    fun provideEffectRecord(
        controllerProvider: Provider<EffectController<TestInterface>>,
    ): EffectRecord {
        return EffectRecord(
            effectImplementationClass = TestClass1::class,
            effectInterfaceClass = TestInterface::class,
            controllerProvider = { controllerProvider.get() },
        )
    }

}