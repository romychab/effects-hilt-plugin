import com.elveum.effects.annotations.CustomEffect

interface BaseInterface

@CustomEffect
sealed class TestSealedClass : BaseInterface {
    data object Test : TestSealedClass()
}
