import com.elveum.effects.annotations.CustomEffect
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Callable

interface TestInterface {
    fun <K, T : Callable<K>> flowEvent(arg1: String, arg2: T): Flow<T>
}

@CustomEffect
class TestClass : TestInterface {
    override fun <K, T : Callable<K>> flowEvent(arg1: String, arg2: T): Flow<T> {
        return TODO()
    }
}
