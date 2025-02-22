import com.elveum.effects.annotations.HiltEffect
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Singleton

interface TestInterface {
    fun oneTimeEvent(arg1: String, arg2: Int)
}

@HiltEffect(
    installIn = Singleton::class
)
class TestClass : TestInterface {
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}

@HiltAndroidApp
class App
