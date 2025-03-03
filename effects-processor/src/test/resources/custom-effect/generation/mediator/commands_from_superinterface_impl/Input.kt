import com.elveum.effects.annotations.HiltEffect
import dagger.hilt.android.HiltAndroidApp

interface SuperInterface {
    fun abstractSuperMethod(arg: String)
}

interface TestInterface : SuperInterface {
    fun oneTimeEvent(arg1: String, arg2: Int)
}

@HiltEffect
class TestClass : TestInterface {
    override fun abstractSuperMethod(arg: String) = Unit
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}

@HiltAndroidApp
class App
