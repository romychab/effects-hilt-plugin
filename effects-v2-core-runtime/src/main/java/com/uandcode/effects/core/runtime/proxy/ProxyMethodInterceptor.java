package com.uandcode.effects.core.runtime.proxy;

import com.uandcode.effects.core.CommandExecutor;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import java.lang.reflect.Method;
import kotlinx.coroutines.flow.Flow;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

public class ProxyMethodInterceptor<Effect> {

    private final CommandExecutor<Effect> commandExecutor;
    private final String cleanUpMethodName;

    public ProxyMethodInterceptor(
        CommandExecutor<Effect> commandExecutor,
        String cleanUpMethodName
    ) {
        this.commandExecutor = commandExecutor;
        this.cleanUpMethodName = cleanUpMethodName;
    }

    @SuppressWarnings({"unused", "unchecked"})
    @RuntimeType
    public Object run(
        @AllArguments Object[] args,
        @Origin final Method method
    ) {
        ExecutionContext<Effect> executionContext = new ExecutionContext<>(method, args, commandExecutor);
        if (isCleanUp(method)) {
            close();
        } else if (isCoroutine(method)) {
            return executionContext.executeCoroutine();
        } else if (isFlow(method)) {
            return executionContext.executeFlow();
        } else if (isUnit(method)) {
            executionContext.executeUnitCommand();
        } else {
            throw new IllegalArgumentException(
                "Invalid method definition in the effect interface.\n" +
                "1. Make sure all methods with a return type have a 'suspend' modifier.\n" +
                "2. Make sure methods that return Flow<T> don't have a 'suspend' modifier."
            );
        }
        return Unit.INSTANCE;
    }

    @SuppressWarnings("unused")
    public void close() {
        commandExecutor.cleanUp();
    }

    private boolean isUnit(Method method) {
        return method.getReturnType() == Unit.class
                || method.getReturnType() == void.class;
    }

    private boolean isFlow(Method method) {
        return method.getReturnType() == Flow.class;
    }

    private boolean isCoroutine(Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) {
            return false;
        }
        return paramTypes[paramTypes.length - 1] == Continuation.class;
    }

    private boolean isCleanUp(Method method) {
        return method.getName().equals(cleanUpMethodName)
                && method.getParameterCount() == 0
                && method.getReturnType() == void.class;
    }

}
