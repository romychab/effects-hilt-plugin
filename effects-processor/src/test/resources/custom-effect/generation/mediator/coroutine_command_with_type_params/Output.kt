import com.elveum.effects.core.v2.CommandExecutor
import java.util.concurrent.Callable
import kotlin.String

public class TestInterfaceMediator(
    private val commandExecutor: CommandExecutor<TestClass>,
) : TestInterface {

    public override suspend fun <K, T : Callable<K>> coroutineEvent(arg1: String, arg2: T): T =
        commandExecutor.executeCoroutine { it.coroutineEvent(arg1, arg2) }

}