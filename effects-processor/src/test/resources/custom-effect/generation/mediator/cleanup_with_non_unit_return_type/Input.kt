import com.elveum.effects.annotations.HiltEffect
import dagger.hilt.android.HiltAndroidApp

interface TestInterface {
    fun oneTimeEvent(arg1: String, arg2: Int)
    fun cleanUp(): String = ""
}

@HiltEffect
class TestClass : TestInterface {
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}

@HiltAndroidApp
class App
