import com.elveum.effects.annotations.HiltEffect
import dagger.hilt.android.HiltAndroidApp

interface TestInterface {
    suspend fun coroutineEvent(arg1: String, arg2: Int): Number
}

@HiltEffect
class TestClass : TestInterface {
    override suspend fun coroutineEvent(arg1: String, arg2: Int): Number {
        return TODO()
    }
}

@HiltAndroidApp
class App
