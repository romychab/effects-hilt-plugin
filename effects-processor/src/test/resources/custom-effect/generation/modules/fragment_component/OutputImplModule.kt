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
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object TestClassImplModule {

    @Provides
    fun provideCleanerOfEffectImpl(
        observableResourceStore: ObservableResourceStore<TestInterface>,
    ): EffectCleaner<TestClass> {
        return EffectCleanerImpl(observableResourceStore)
    }

    @Provides
    fun provideControllerOfEffectImpl(
        observableResourceStore: ObservableResourceStore<TestInterface>
    ): EffectController<TestClass> {
        return EffectControllerImpl(observableResourceStore)
    }

    @Provides
    @IntoSet
    fun provideEffectRecord(
        controllerProvider: Provider<EffectController<TestInterface>>,
    ): EffectRecord {
        return EffectRecord(
            effectImplementationClass = TestClass::class,
            effectInterfaceClass = TestInterface::class,
            controllerProvider = { controllerProvider.get() },
        )
    }

}