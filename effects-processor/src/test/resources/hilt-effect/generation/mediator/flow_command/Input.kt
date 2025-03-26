import com.elveum.effects.annotations.HiltEffect
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.Flow

interface TestInterface {
    fun flowEvent(arg1: String, arg2: Int): Flow<Number>
}

@HiltEffect
class TestClass : TestInterface {
    override fun flowEvent(arg1: String, arg2: Int): Flow<Number> {
        return TODO()
    }
}

@HiltAndroidApp
class App
