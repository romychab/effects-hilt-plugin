import com.elveum.effects.annotations.CustomEffect
import dagger.hilt.android.components.FragmentComponent

interface TestInterface {
    fun oneTimeEvent(arg1: String, arg2: Int)
}

@CustomEffect(
    installIn = FragmentComponent::class
)
class TestClass : TestInterface {
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}
