package gg.mineral.bot.impl.math.optimization;

import java.util.function.Function;

import org.apache.commons.math3.optim.MaxEval;

import org.eclipse.jdt.annotation.NonNull;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import gg.mineral.bot.api.math.optimization.RecursiveCalculation;
import gg.mineral.bot.api.world.ClientWorld;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;

import java.util.concurrent.TimeUnit;

public abstract class Optimizer<C extends RecursiveCalculation<?>, V>
        implements gg.mineral.bot.api.math.optimization.Optimizer<C, V> {

    private static Cache<Class<? extends RecursiveCalculation<?>>, RecursiveCalculation<?>> constructorCache = Caffeine
            .newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @Getter
    protected final Function<C, Number> valueFunction;
    protected final MaxEval maxEval;
    @NonNull
    private final C calculation;
    protected final Param<?>[] params;

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public Optimizer(ClientWorld world, Class<C> clazz, Function<C, Number> valueFunction, int maxEval,
            Param<?>... params) {
        this.params = params;
        this.valueFunction = valueFunction;
        this.maxEval = new MaxEval(maxEval);
        this.calculation = (C) constructorCache.get(clazz,
                k -> newCalcInstance(clazz, world));
    }

    @SneakyThrows
    private static RecursiveCalculation<?> newCalcInstance(Class<? extends RecursiveCalculation<?>> clazz,
            ClientWorld world) {
        return clazz.getDeclaredConstructor(ClientWorld.class).newInstance(world);
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
            if (params[i] instanceof Const<?> c)
                args[i] = c.value();

        return args;
    }
}
