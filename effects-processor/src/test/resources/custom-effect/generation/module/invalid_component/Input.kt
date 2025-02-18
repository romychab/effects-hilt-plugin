import com.elveum.effects.annotations.CustomEffect
import javax.inject.Singleton

interface TestInterface {
    fun oneTimeEvent(arg1: String, arg2: Int)
}

@CustomEffect(
    installIn = Singleton::class
)
class TestClass : TestInterface {
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}
