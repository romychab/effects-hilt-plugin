package com.uandcode.effects.core.runtime.proxy;

import com.uandcode.effects.core.CommandExecutor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

public class ProxyMethodInterceptor<Effect> implements InvocationHandler {

    private final CommandExecutor<Effect> commandExecutor;

    public ProxyMethodInterceptor(
        CommandExecutor<Effect> commandExecutor
    ) {
        this.commandExecutor = commandExecutor;
    }

    @Override
    public Object invoke(
        Object proxyObject,
        Method method,
        Object[] args
    ) {
        ExecutionContext<Effect> executionContext = new ExecutionContext<>(method, args, commandExecutor);
        if (method.getDeclaringClass().equals(AutoCloseable.class)) {
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

}
