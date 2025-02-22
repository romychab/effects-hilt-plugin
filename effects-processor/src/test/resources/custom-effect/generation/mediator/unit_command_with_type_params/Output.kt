import com.elveum.effects.core.v2.CommandExecutor
import java.util.concurrent.Callable
import kotlin.String

public class __TestInterfaceMediator(
    private val commandExecutor: CommandExecutor<TestInterface>,
) : TestInterface {
    public override fun <K, T : Callable<K>> oneTimeEvent(arg1: String, arg2: T) {
        commandExecutor.execute {
            it.oneTimeEvent(arg1, arg2)
        }
    }
}