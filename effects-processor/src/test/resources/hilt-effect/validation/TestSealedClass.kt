import com.elveum.effects.annotations.HiltEffect

interface BaseInterface

@HiltEffect
sealed class TestSealedClass : BaseInterface {
    data object Test : TestSealedClass()
}
