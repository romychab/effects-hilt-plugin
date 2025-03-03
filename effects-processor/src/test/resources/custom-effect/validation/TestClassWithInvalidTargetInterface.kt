import com.elveum.effects.annotations.HiltEffect

interface BaseInterface

interface ValidInterface1
interface ValidInterface2
interface InvalidInterface3

@HiltEffect(
    target = InvalidInterface3::class,
)
class TestClassWithInvalidTargetInterface : ValidInterface1, ValidInterface2
