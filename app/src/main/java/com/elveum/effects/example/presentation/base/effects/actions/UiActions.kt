package com.elveum.effects.example.presentation.base.effects.actions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

interface UiActions {
    fun <T : Action> listenActions(actionType: KClass<T>): Flow<T>
}

inline fun <reified T : Action> UiActions.listenActions(): Flow<T> {
    return listenActions(T::class)
}

inline fun <reified T : Action> UiActions.handleActions(
    viewModelScope: CoroutineScope,
    crossinline handler: (T) -> Unit,
) {
    viewModelScope.launch {
        listenActions<T>().collect {
            handler(it)
        }
    }
}
