package com.uandcode.effects.core.testing.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.StateFlow

public class TestLifecycleOwner : Lifecycle(), LifecycleOwner {

    private val observers = mutableSetOf<LifecycleObserver>()
    private var _previousState = State.INITIALIZED
    private var _currentState = State.INITIALIZED
    private var lastNotifier: ((LifecycleObserver) -> Unit)? = null

    override val lifecycle: Lifecycle = this
    override val currentState: State get() = _currentState

    override fun addObserver(observer: LifecycleObserver) {
        observers.add(observer)
        notifyObservers()
    }

    override fun removeObserver(observer: LifecycleObserver) {
        observers.remove(observer)
    }

    public fun create(): Unit = changeState(State.CREATED)
    public fun start(): Unit = changeState(State.STARTED)
    public fun resume(): Unit = changeState(State.RESUMED)
    public fun pause(): Unit = changeState(State.STARTED)
    public fun stop(): Unit = changeState(State.CREATED)
    public fun destroy(): Unit = changeState(State.INITIALIZED)

    override val currentStateFlow: StateFlow<State>
        get() {
            println("get current flow")
            return super.currentStateFlow
        }

    private fun changeState(newState: State) {
        val indexOfOldState = stateChain.indexOf(_currentState)
        val indexOfNewState = stateChain.indexOf(newState)
        _previousState = _currentState
        _currentState = newState
        lastNotifier = {
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
        notifyObservers()
    }

    private fun notifyObservers() {
        lastNotifier?.let(observers::forEach)
    }

    private fun notifyForward(state: State, observer: LifecycleObserver) {
        if (observer is DefaultLifecycleObserver) {
            when (state) {
                State.CREATED -> observer.onCreate(this)
                State.STARTED -> observer.onStart(this)
                State.RESUMED -> observer.onResume(this)
                else -> {}
            }
        } else if (observer is LifecycleEventObserver) {
            when (state) {
                State.CREATED -> observer.onStateChanged(this, Lifecycle.Event.ON_CREATE)
                State.STARTED -> observer.onStateChanged(this, Lifecycle.Event.ON_START)
                State.RESUMED -> observer.onStateChanged(this, Lifecycle.Event.ON_RESUME)
                else -> {}
            }
        }
    }

    private fun notifyBackward(state: State, observer: LifecycleObserver) {
        if (observer is DefaultLifecycleObserver) {
            when (state) {
                State.STARTED -> observer.onPause(this)
                State.CREATED -> observer.onStop(this)
                State.INITIALIZED -> observer.onDestroy(this)
                else -> {}
            }
        } else if (observer is LifecycleEventObserver) {
            when (state) {
                State.STARTED -> observer.onStateChanged(this, Lifecycle.Event.ON_PAUSE)
                State.CREATED -> observer.onStateChanged(this, Lifecycle.Event.ON_STOP)
                State.INITIALIZED -> observer.onStateChanged(this, Lifecycle.Event.ON_DESTROY)
                else -> {}
            }
        }
    }

    private companion object {
        val stateChain = listOf(
            State.INITIALIZED, State.CREATED, State.STARTED, State.RESUMED,
        )
    }

}
