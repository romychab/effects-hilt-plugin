import com.elveum.effects.annotations.CustomEffect

interface SuperInterface {
    fun abstractSuperMethod(arg: String)
}

interface TestInterface : SuperInterface {
    fun oneTimeEvent(arg1: String, arg2: Int)
}

@CustomEffect
class TestClass : TestInterface {
    override fun abstractSuperMethod(arg: String) = Unit
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}