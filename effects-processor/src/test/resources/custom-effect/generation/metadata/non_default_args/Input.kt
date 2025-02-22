package test

import com.elveum.effects.annotations.HiltEffect
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent

interface TestInterface {
    fun oneTimeEvent(arg1: String, arg2: Int)
}

interface TestInterface2

@HiltEffect(
    target = TestInterface::class,
    installIn = FragmentComponent::class,
)
class TestClass : TestInterface2, TestInterface {
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}

@HiltAndroidApp
class App
