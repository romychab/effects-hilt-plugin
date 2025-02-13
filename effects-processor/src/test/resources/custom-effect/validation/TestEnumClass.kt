import com.elveum.effects.annotations.CustomEffect

interface BaseInterface

@CustomEffect
enum class TestEnumClass : BaseInterface {
    One, Two;
}
