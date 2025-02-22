import com.elveum.effects.annotations.HiltEffect
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.Callable

interface TestInterface {
    suspend fun <K, T : Callable<K>> coroutineEvent(arg1: String, arg2: T): T
}

@HiltEffect
class TestClass : TestInterface {
    override suspend fun <K, T : Callable<K>> coroutineEvent(arg1: String, arg2: T): T {
        return TODO()
    }
}

@HiltAndroidApp
class App
