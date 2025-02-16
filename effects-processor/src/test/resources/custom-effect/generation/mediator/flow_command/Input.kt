import com.elveum.effects.annotations.CustomEffect
import kotlinx.coroutines.flow.Flow

interface TestInterface {
    fun flowEvent(arg1: String, arg2: Int): Flow<Number>
}

@CustomEffect
class TestClass : TestInterface {
    override suspend fun flowEvent(arg1: String, arg2: Int): Flow<Number> {
        return TODO()
    }
}
