import com.elveum.effects.annotations.CustomEffect

interface TestInterface {
    suspend fun coroutineEvent(arg1: String, arg2: Int): Number
}

@CustomEffect
class TestClass : TestInterface {
    override suspend fun coroutineEvent(arg1: String, arg2: Int): Number {
        return TODO()
    }
}