import com.elveum.effects.annotations.HiltEffect

interface BaseInterface

@HiltEffect
enum class TestEnumClass : BaseInterface {
    One, Two;
}
