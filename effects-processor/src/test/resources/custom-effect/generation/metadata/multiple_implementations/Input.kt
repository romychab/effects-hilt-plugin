package test

import com.elveum.effects.annotations.HiltEffect
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ActivityComponent

interface TestInterface {
    fun oneTimeEvent(arg1: String, arg2: Int)
}

@HiltEffect
class TestClass1 : TestInterface {
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}

@HiltEffect
class TestClass2 : TestInterface {
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}

@HiltAndroidApp
class App
