import com.elveum.effects.annotations.CustomEffect
import dagger.hilt.components.SingletonComponent

interface TestInterface {
    fun oneTimeEvent(arg1: String, arg2: Int)
}

@CustomEffect(
    installIn = SingletonComponent::class
)
class TestClass : TestInterface {
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}
