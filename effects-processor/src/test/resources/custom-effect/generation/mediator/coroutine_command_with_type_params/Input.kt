import com.elveum.effects.annotations.CustomEffect
import java.util.concurrent.Callable

interface TestInterface {
    suspend fun <K, T : Callable<K>> coroutineEvent(arg1: String, arg2: T): T
}

@CustomEffect
class TestClass : TestInterface {
    override suspend fun <K, T : Callable<K>> coroutineEvent(arg1: String, arg2: T): T {
        return TODO()
    }
}