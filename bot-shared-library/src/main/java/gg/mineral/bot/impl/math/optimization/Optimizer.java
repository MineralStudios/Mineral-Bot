package gg.mineral.bot.impl.math.optimization;

import gg.mineral.bot.api.math.optimization.RecursiveCalculation;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.math3.optim.MaxEval;
import org.eclipse.jdt.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.function.Function;

public abstract class Optimizer<C extends RecursiveCalculation, V>
        implements gg.mineral.bot.api.math.optimization.Optimizer<C, V> {
    @Getter
    protected final Function<C, Number> valueFunction;
    protected final MaxEval maxEval;
    @NonNull
    private final C calculation;
    protected final Param<?>[] params;

    @SneakyThrows
    public Optimizer(Callable<C> callable, Function<C, Number> valueFunction, int maxEval,
                     Param<?>... params) {
        this.params = params;
        this.valueFunction = valueFunction;
        this.maxEval = new MaxEval(maxEval);
        this.calculation = callable.call();
    }

    @Override
    public int getMaxEval() {
        return maxEval.getMaxEval();
    }

    @SuppressWarnings("unchecked")
    @Override
    @SneakyThrows
    public C newCalculation(Object... args) {
        return (C) calculation.initialize(args);
    }

    protected Object[] getArgs() {
        val args = new Object[params.length];

        for (int i = 0; i < params.length; i++)
            if (params[i] instanceof Const<?>(Object value))
                args[i] = value;

        return args;
    }
}
