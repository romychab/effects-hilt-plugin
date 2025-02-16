import com.elveum.effects.core.v2.CommandExecutor
import kotlin.Int
import kotlin.Number
import kotlin.String

public class TestInterfaceMediator(
    private val commandExecutor: CommandExecutor<TestClass>,
) : TestInterface {
    public override suspend fun coroutineEvent(arg1: String, arg2: Int): Number =
        commandExecutor.executeCoroutine { it.coroutineEvent(arg1, arg2) }
}
