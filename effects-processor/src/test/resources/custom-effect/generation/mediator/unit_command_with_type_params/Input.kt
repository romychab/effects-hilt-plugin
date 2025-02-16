import com.elveum.effects.annotations.CustomEffect
import java.util.concurrent.Callable

interface TestInterface {
    fun <K, T : Callable<K>> oneTimeEvent(arg1: String, arg2: T)
}

@CustomEffect
class TestClass : TestInterface {
    override fun <K, T : Callable<K>> oneTimeEvent(arg1: String, arg2: T) = Unit
}