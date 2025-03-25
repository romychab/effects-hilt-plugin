import com.elveum.effects.core.CommandExecutor
import java.util.concurrent.Callable
import javax.inject.Inject
import kotlin.String
import kotlinx.coroutines.flow.Flow

public class __TestInterfaceMediator @Inject constructor(
  private val commandExecutor: CommandExecutor<TestInterface>,
) : TestInterface {
  public override fun <K, T : Callable<K>> flowEvent(arg1: String, arg2: T): Flow<T> {
     return commandExecutor.executeFlow {
        it.flowEvent(arg1, arg2)
     }
  }

  public fun __internalCleanUp() {
    commandExecutor.cleanUp()
  }
}