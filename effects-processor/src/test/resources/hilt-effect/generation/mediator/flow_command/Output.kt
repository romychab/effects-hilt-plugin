import com.elveum.effects.core.CommandExecutor
import javax.inject.Inject
import kotlin.Int
import kotlin.Number
import kotlin.String
import kotlinx.coroutines.flow.Flow

public class __TestInterfaceMediator @Inject constructor(
  private val commandExecutor: CommandExecutor<TestInterface>,
) : TestInterface {
  public override fun flowEvent(arg1: String, arg2: Int): Flow<Number> {
     return commandExecutor.executeFlow {
        it.flowEvent(arg1, arg2)
     }
  }

  public fun __internalCleanUp() {
    commandExecutor.cleanUp()
  }
}