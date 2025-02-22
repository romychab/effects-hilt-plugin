import com.elveum.effects.annotations.HiltEffect
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.Callable

interface TestInterface {
    fun <K, T : Callable<K>> oneTimeEvent(arg1: String, arg2: T)
}

@HiltEffect
class TestClass : TestInterface {
    override fun <K, T : Callable<K>> oneTimeEvent(arg1: String, arg2: T) = Unit
}

@HiltAndroidApp
class App
