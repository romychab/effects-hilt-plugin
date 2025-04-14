package com.uandcode.effects.koin.internal

internal class ConstructorArgStack {

    private val args = ArrayDeque<ConstructorArg>()

    fun push(arg: ConstructorArg): ConstructorArg {
        args.addLast(arg)
        return arg
    }

    fun peek(): ConstructorArg? {
        return args.lastOrNull()
    }

    fun clear() {
        args.clear()
    }

    fun pop() {
        args.removeLastOrNull()
    }

    fun popTo(arg: ConstructorArg): List<ConstructorArg> {
        val index = args.indexOf(arg)
        if (index == -1 || index == args.lastIndex) return emptyList()
        val subList = args.subList(index + 1, args.size).toList()
        args.removeAll(subList)
        return subList.filter { it.hasCloseable() }
    }

    fun count(): Int {
        return args.size
    }

}
