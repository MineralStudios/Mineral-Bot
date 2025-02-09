package gg.mineral.bot.api.math.optimization;

import lombok.val;
import org.eclipse.jdt.annotation.NonNull;

import java.util.LinkedList;
import java.util.function.Function;

public interface Optimizer<C extends RecursiveCalculation, V> {

    abstract class Data<C extends RecursiveCalculation, V> {
        protected final LinkedList<Object> queue = new LinkedList<>();

        public Data<C, V> val(Object... args) {
            for (val arg : args)
                queue.add(arg instanceof Number num ? Param.constant(num) : Param.obj(arg));

            return this;
        }

        public Data<C, V> var(Number lowerBound, Number upperBound) {
            val lower = Math.min(lowerBound.doubleValue(), upperBound.doubleValue());
            val upper = Math.max(lowerBound.doubleValue(), upperBound.doubleValue());
            queue.add(Param.var(lower, upper));
            return this;
        }

        public abstract Optimizer<C, V> build();
    }

    interface Param<T> {
        /**
         * Gets the type of the parameter.
         *
         * @return the type of the parameter
         */
        Class<?> getType();

        static <T extends Number> Const<T> constant(@NonNull T value) {
            return new Const<>(value);
        }

        static <T extends Number> IndependentVar<T> var(@NonNull T lowerBound, @NonNull T upperBound) {
            return new IndependentVar<>(lowerBound, upperBound);
        }

        static <T> Obj<T> obj(@NonNull T value) {
            return new Obj<>(value);
        }
    }

    record Const<T extends Number>(@NonNull T value) implements Param<T> {
        @Override
        public Class<? extends Number> getType() {
            return value.getClass();
        }
    }

    record IndependentVar<T extends Number>(@NonNull T lowerBound, @NonNull T upperBound)
            implements Param<T> {

        @Override
        public Class<? extends Number> getType() {
            return lowerBound.getClass();
        }
    }

    record Obj<T>(@NonNull T value) implements Param<T> {

        @Override
        public Class<?> getType() {
            return value.getClass();
        }
    }

    /**
     * Returns the value to optimize.
     *
     * @return the value to optimize
     */
    Function<C, Number> getValueFunction();

    /**
     * The maximum number of evaluations.
     *
     * @return the maximum number of evaluations
     */
    int getMaxEval();

    /**
     * Creates a new calculation.
     *
     * @return the calculation
     */
    C newCalculation(Object... args);

    /**
     * Finds the minimum of the function.
     *
     * @return the result
     */
    V minimize();

    /**
     * Finds the maximum of the function.
     *
     * @return the result
     */
    V maximize();
}
