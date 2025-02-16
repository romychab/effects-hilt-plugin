import com.elveum.effects.core.v2.CommandExecutor
import kotlin.Int
import kotlin.Number
import kotlin.String
import kotlinx.coroutines.flow.Flow

public class TestInterfaceMediator(
    private val commandExecutor: CommandExecutor<TestClass>,
) : TestInterface {

    public override fun flowEvent(arg1: String, arg2: Int): Flow<Number> = commandExecutor.executeFlow
        { it.flowEvent(arg1, arg2) }

}
