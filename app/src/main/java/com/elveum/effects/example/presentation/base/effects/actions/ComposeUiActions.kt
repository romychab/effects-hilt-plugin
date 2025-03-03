package com.elveum.effects.example.presentation.base.effects.actions

import androidx.compose.runtime.Stable
import com.elveum.effects.annotations.HiltEffect
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.reflect.KClass

@HiltEffect
@Stable
class ComposeUiActions : UiActions {

    private val actionFlow = MutableSharedFlow<Action>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override fun <T : Action> listenActions(actionType: KClass<T>): Flow<T> {
        return actionFlow.filterIsInstance(actionType)
    }

    fun submitAction(action: Action) {
        actionFlow.tryEmit(action)
    }

}