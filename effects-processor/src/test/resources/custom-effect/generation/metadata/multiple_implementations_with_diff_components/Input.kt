package test

import com.elveum.effects.annotations.HiltEffect
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent

interface TestInterface {
    fun oneTimeEvent(arg1: String, arg2: Int)
}

@HiltEffect(
    installIn = ActivityComponent::class,
)
class TestClass1 : TestInterface {
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}

@HiltEffect(
    installIn = SingletonComponent::class,
)
class TestClass2 : TestInterface {
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}

@HiltAndroidApp
class App
