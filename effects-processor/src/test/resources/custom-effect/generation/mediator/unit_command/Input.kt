import com.elveum.effects.annotations.CustomEffect

interface TestInterface {
    fun oneTimeEvent(arg1: String, arg2: Int)
}

@CustomEffect
class TestClass : TestInterface {
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}