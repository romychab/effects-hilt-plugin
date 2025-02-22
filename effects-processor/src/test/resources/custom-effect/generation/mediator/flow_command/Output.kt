import com.elveum.effects.core.v2.CommandExecutor
import kotlin.Int
import kotlin.Number
import kotlin.String
import kotlinx.coroutines.flow.Flow

public class __TestInterfaceMediator(
    private val commandExecutor: CommandExecutor<TestInterface>,
) : TestInterface {

    public override fun flowEvent(arg1: String, arg2: Int): Flow<Number> {
        return commandExecutor.executeFlow {
            it.flowEvent(arg1, arg2)
        }
    }

}
