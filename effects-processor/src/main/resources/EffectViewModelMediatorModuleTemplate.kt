%PACKAGE_STATEMENT%

import com.elveum.effects.core.HiltOverridable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.ViewModelLifecycle
import dagger.hilt.android.components.ViewModelComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ViewModelComponent::class)
internal object %CLASSNAME% {

    @Provides
    @IntoSet
    fun provideMediatorIntoSet(
        viewModelLifecycle: ViewModelLifecycle,
        originMediator: %ORIGIN_MEDIATOR_NAME%,
    ): HiltOverridable<%TARGET_INTERFACE_NAME%> {
        val mediator = %VIEW_MODEL_MEDIATOR_NAME%(originMediator, viewModelLifecycle)
        return HiltOverridable(mediator, HiltOverridable.%PRIORITY_CONST%)
    }

}
