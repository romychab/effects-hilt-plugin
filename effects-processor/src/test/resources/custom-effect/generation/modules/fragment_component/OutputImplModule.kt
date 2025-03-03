import com.elveum.effects.core.EffectController
import com.elveum.effects.core.EffectRecord
import com.elveum.effects.core.ObservableResourceStore
import com.elveum.effects.core.impl.EffectControllerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.multibindings.IntoSet
import javax.inject.Provider
import TestInterface
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object TestClassImplModule {

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