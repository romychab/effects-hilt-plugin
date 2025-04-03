package com.uandcode.effects.core.runtime;

import com.uandcode.effects.core.CommandExecutor;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import kotlinx.coroutines.flow.Flow;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

public class ProxyMethodInterceptor<Effect> {

    private final CommandExecutor<Effect> commandExecutor;

    ProxyMethodInterceptor(CommandExecutor<Effect> commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @SuppressWarnings({"unused", "unchecked"})
    @RuntimeType
    public Object run(
        @AllArguments Object[] args,
        @Origin final Method method
    ) {
        if (isUnit(method)) {
            commandExecutor.execute(effect -> {
                execute(effect, method, args);
                return Unit.INSTANCE;
            });
            return Unit.INSTANCE;
        } else if (isCoroutine(method)) {
            Continuation<Object> continuation = (Continuation<Object>) args[args.length - 1];
            return commandExecutor.executeCoroutine(
                (effect, nextContinuation) -> {
                    Object[] modifiedArgs = Arrays.copyOf(args, args.length);
                    modifiedArgs[modifiedArgs.length - 1] = nextContinuation;
                    return execute(effect, method, modifiedArgs);
                },
                continuation
            );
        } else if (isFlow(method)) {
            return commandExecutor.executeFlow(
                effect -> (Flow<?>) execute(effect, method, args)
            );
        } else {
            throw new IllegalArgumentException(
                "Invalid method definition in the effect interface.\n" +
                "1. Make sure all methods with a return type have a 'suspend' modifier.\n" +
                "2. Make sure methods that return Flow<T> don't have a 'suspend' modifier."
            );
        }
    }

    @SuppressWarnings("unused")
    public void close() {
        commandExecutor.cleanUp();
    }

    private Object execute(Effect effect, Method method, Object[] args) {
        try {
            Method targetMethod = effect.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
            return targetMethod.invoke(effect, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Can't invoke method " + method.getName(), e);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        }
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
