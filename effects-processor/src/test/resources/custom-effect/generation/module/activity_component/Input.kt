import com.elveum.effects.annotations.CustomEffect
import dagger.hilt.android.components.ActivityComponent

interface TestInterface {
    fun oneTimeEvent(arg1: String, arg2: Int)
}

@CustomEffect(
    installIn = ActivityComponent::class
)
class TestClass : TestInterface {
    override fun oneTimeEvent(arg1: String, arg2: Int) = Unit
}
