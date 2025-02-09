package gg.mineral.bot.impl.math.optimization;

import gg.mineral.bot.api.math.optimization.RecursiveCalculation;
import lombok.val;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateFunctionMappingAdapter;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class BivariateOptimizer<C extends RecursiveCalculation> extends Optimizer<C, Number[]>
        implements gg.mineral.bot.api.math.optimization.BivariateOptimizer<C> {
    private final int objectiveVarIndex1, objectiveVarIndex2;

    public BivariateOptimizer(Callable<C> callable, Function<C, Number> valueFunction, int maxEval,
                              Param<?>[] params) {
        super(callable, valueFunction, maxEval, params);

        var objectiveVarIndex1 = -1;
        var objectiveVarIndex2 = -1;

        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof IndependentVar && objectiveVarIndex1 == -1) {
                objectiveVarIndex1 = i;
                continue;
            }

            if (params[i] instanceof IndependentVar && objectiveVarIndex2 == -1) {
                objectiveVarIndex2 = i;
                break;
            }
        }

        this.objectiveVarIndex1 = objectiveVarIndex1;
        this.objectiveVarIndex2 = objectiveVarIndex2;
    }

    @Override
    public Number[] minimize() {
        val args = getArgs();

        val objectiveVar1 = params[objectiveVarIndex1];
        val objectiveVar2 = params[objectiveVarIndex2];
        if (objectiveVar1 instanceof IndependentVar(
                Number lowerBound, Number upperBound
        ) && objectiveVar2 instanceof IndependentVar(
                Number lowerBound1, Number upperBound1
        )) {
            val lowerBounds = new double[]{lowerBound.doubleValue(), lowerBound1.doubleValue()};
            val upperBounds = new double[]{upperBound.doubleValue(), upperBound1.doubleValue()};

            final var boundedFunction = getMultivariateFunctionMappingAdapter(args, lowerBounds, upperBounds, Double.MAX_VALUE);
            val simpleBounds = new SimpleBounds(lowerBounds, upperBounds);
            val optimizer = new BOBYQAOptimizer(5);
            val result = optimizer.optimize(
                    maxEval,
                    new InitialGuess(new double[]{0, 0}),
                    new ObjectiveFunction(boundedFunction),
                    GoalType.MINIMIZE,
                    simpleBounds);

            val point = result.getPoint();

            return new Number[]{point[0], point[1]};
        }

        return new Number[]{-1, -1};
    }

    private MultivariateFunctionMappingAdapter getMultivariateFunctionMappingAdapter(Object[] args, double[] lowerBounds, double[] upperBounds, double defaultValue) {
        MultivariateFunction objective = point -> {
            args[objectiveVarIndex1] = point[0];
            args[objectiveVarIndex2] = point[1];
            val calc = newCalculation(args);
            if (calc.compute(maxEval.getMaxEval()) == RecursiveCalculation.Result.VALID)
                return valueFunction.apply(calc).doubleValue();
            else return defaultValue;
        };

        return new MultivariateFunctionMappingAdapter(objective, lowerBounds, upperBounds);
    }

    @Override
    public Number[] maximize() {
        val args = getArgs();

        val objectiveVar1 = params[objectiveVarIndex1];
        val objectiveVar2 = params[objectiveVarIndex2];
        if (objectiveVar1 instanceof IndependentVar(
                Number lowerBound, Number upperBound
        ) && objectiveVar2 instanceof IndependentVar(
                Number lowerBound1, Number upperBound1
        )) {
            val lowerBounds = new double[]{lowerBound.doubleValue(), lowerBound1.doubleValue()};
            val upperBounds = new double[]{upperBound.doubleValue(), upperBound1.doubleValue()};

            final var boundedFunction = getMultivariateFunctionMappingAdapter(args, lowerBounds, upperBounds, Double.MIN_VALUE);
            val simpleBounds = new SimpleBounds(lowerBounds, upperBounds);
            val optimizer = new BOBYQAOptimizer(5);
            val result = optimizer.optimize(
                    maxEval,
                    new InitialGuess(new double[]{0, 0}),
                    new ObjectiveFunction(boundedFunction),
                    GoalType.MAXIMIZE,
                    simpleBounds);

            val point = result.getPoint();

            return new Number[]{point[0], point[1]};
        }

        return new Number[]{-1, -1};
    }

}
