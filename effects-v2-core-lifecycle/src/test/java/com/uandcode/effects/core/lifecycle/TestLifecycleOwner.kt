package com.uandcode.effects.core.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

class TestLifecycleOwner : Lifecycle(), LifecycleOwner {

    private val observers = mutableSetOf<LifecycleObserver>()
    private var _previousState = State.INITIALIZED
    private var _currentState = State.INITIALIZED

    override val lifecycle: Lifecycle = this
    override val currentState: State get() = _currentState

    override fun addObserver(observer: LifecycleObserver) {
        observers.add(observer)
    }

    override fun removeObserver(observer: LifecycleObserver) {
        observers.remove(observer)
    }

    fun create() = changeState(State.CREATED)
    fun start() = changeState(State.STARTED)
    fun resume() = changeState(State.RESUMED)
    fun pause() = changeState(State.STARTED)
    fun stop() = changeState(State.CREATED)
    fun destroy() = changeState(State.INITIALIZED)

    private fun changeState(newState: State) {
        val indexOfOldState = stateChain.indexOf(_currentState)
        val indexOfNewState = stateChain.indexOf(newState)
        _previousState = _currentState
        _currentState = newState
        val notifier: (DefaultLifecycleObserver) -> Unit = {
            if (indexOfOldState < indexOfNewState) {
                for (i in indexOfOldState + 1..indexOfNewState) {
                    notifyForward(stateChain[i], it)
                }
            } else if (indexOfOldState > indexOfNewState) {
                for (i in indexOfOldState - 1 downTo indexOfNewState) {
                    notifyBackward(stateChain[i], it)
                }
            }
        }
        observers
            .filterIsInstance<DefaultLifecycleObserver>()
            .forEach(notifier)
    }

    private fun notifyForward(state: State, observer: DefaultLifecycleObserver) {
        when (state) {
            State.CREATED -> observer.onCreate(this)
            State.STARTED -> observer.onStart(this)
            State.RESUMED -> observer.onResume(this)
            else -> {}
        }
    }

    private fun notifyBackward(state: State, observer: DefaultLifecycleObserver) {
        when (state) {
            State.STARTED -> observer.onPause(this)
            State.CREATED -> observer.onStop(this)
            State.INITIALIZED -> observer.onDestroy(this)
            else -> {}
        }
    }

    private companion object {
        val stateChain = listOf(
            State.INITIALIZED, State.CREATED, State.STARTED, State.RESUMED,
        )
    }

}
