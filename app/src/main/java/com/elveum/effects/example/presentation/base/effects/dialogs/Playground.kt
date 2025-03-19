package com.elveum.effects.example.presentation.base.effects.dialogs

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
import dagger.hilt.android.ViewModelLifecycle
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.multibindings.IntoSet
import javax.inject.Inject
import javax.inject.Provider

// --- OLD GENERATED CODE

// --- modified (do not forget Inject annotation)
public class __DialogsMediator @Inject constructor(
    private val commandExecutor: CommandExecutor<Dialogs>,
) : Dialogs {

    // --- new property
    private var isUnitCommandDestroyed = false

    public override suspend fun showAlertDialog(config: AlertDialogConfig): Boolean {
        return commandExecutor.executeCoroutine {
            it.showAlertDialog(config)
        }
    }

    // --- modified
    public override fun toast(message: String) {
        if (isUnitCommandDestroyed) return
        commandExecutor.execute {
            it.toast(message)
        }
    }

    // --- modified
    public override fun cleanUp() {
        __internalCleanUp()
    }

    // --- new method
    public fun __internalCleanUp() {
        isUnitCommandDestroyed = true
        commandExecutor.cleanUp()
    }

}

@Module
@InstallIn(ActivityRetainedComponent::class)
object DialogsEffectModule {

    @Provides
    @ActivityRetainedScoped
    fun provideStore(): ObservableResourceStore<Dialogs> {
        return ObservableResourceStoreImpl()
    }

    @Provides
    fun provideCommandExecutor(resourceStore: ObservableResourceStore<Dialogs>): CommandExecutor<Dialogs> {
        return CommandExecutorImpl(resourceStore)
    }

    // --- new method
    @Provides
    @IntoSet
    fun provideMediatorIntoSet(commandExecutor: CommandExecutor<Dialogs>): Overridable<Dialogs> {
        val mediator = __DialogsMediator(commandExecutor)
        return Overridable(mediator, Overridable.ACTIVITY_RETAINED_PRIORITY)
    }

    // --- modified
    @Provides
    fun provideMediator(set: Set<@JvmSuppressWildcards Overridable<Dialogs>>): Dialogs {
        return set.getInstanceWithMaxPriority()
    }

    @Provides
    fun provideControllerOfEffectInterface(
        observableResourceStore: ObservableResourceStore<Dialogs>
    ): EffectController<Dialogs> {
        return EffectControllerImpl(observableResourceStore)
    }

}

@Module
@InstallIn(ActivityRetainedComponent::class)
object ComposeDialogsImplModule {

    @Provides
    fun provideControllerOfEffectImpl(
        observableResourceStore: ObservableResourceStore<Dialogs>
    ): EffectController<ComposeDialogs> {
        return EffectControllerImpl(observableResourceStore)
    }

    @Provides
    @IntoSet
    fun provideEffectRecord(
        controllerProvider: Provider<EffectController<Dialogs>>,
    ): EffectRecord {
        return EffectRecord(
            effectImplementationClass = ComposeDialogs::class,
            effectInterfaceClass = Dialogs::class,
            controllerProvider = { controllerProvider.get() },
        )
    }

}

// --- CORE MODULE

class Overridable<T>(
    val instance: T,
    val priority: Int,
) {
    companion object {
        const val ACTIVITY_RETAINED_PRIORITY = 1
        const val VIEW_MODEL_PRIORITY = 2
    }
}

fun <T> Set<Overridable<T>>.getInstanceWithMaxPriority(): T {
    val overridableWithMaxPriority = checkNotNull(
        this.maxByOrNull { it.priority }
    )
    return overridableWithMaxPriority.instance
}

// --- NEW GENERATED CODE

// new file
class __DialogsViewModelMediator(
    private val dialogsMediator: __DialogsMediator,
    viewModelLifecycle: ViewModelLifecycle,
) : Dialogs by dialogsMediator {

    init {
        viewModelLifecycle.addOnClearedListener {
            dialogsMediator.__internalCleanUp()
        }
    }

}

// new file
@Module
@InstallIn(ViewModelComponent::class)
object DialogsEffectViewModelModule {

    @Provides
    @IntoSet
    fun provideMediatorIntoSet(
        viewModelLifecycle: ViewModelLifecycle,
        originMediator: __DialogsMediator,
    ): Overridable<Dialogs> {
        val mediator = __DialogsViewModelMediator(originMediator, viewModelLifecycle)
        return Overridable(mediator, Overridable.VIEW_MODEL_PRIORITY)
    }

}
