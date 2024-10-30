package gg.mineral.bot.impl.math.optimization;

import java.util.function.Function;

import org.apache.commons.math3.analysis.MultivariateFunction;

import org.apache.commons.math3.optim.InitialGuess;

import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateFunctionMappingAdapter;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;

import gg.mineral.bot.api.math.optimization.RecursiveCalculation;
import gg.mineral.bot.api.world.ClientWorld;
import lombok.val;

public class BivariateOptimizer<C extends RecursiveCalculation<?>> extends Optimizer<C, Number[]>
        implements gg.mineral.bot.api.math.optimization.BivariateOptimizer<C> {
    private final int objectiveVarIndex1, objectiveVarIndex2;

    public BivariateOptimizer(ClientWorld world, Class<C> clazz, Function<C, Number> valueFunction, int maxEval,
            Param<?>[] params) {
        super(world, clazz, valueFunction, maxEval, params);

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
        if (objectiveVar1 instanceof IndependentVar var1 && objectiveVar2 instanceof IndependentVar var2) {
            val lowerBounds = new double[] { var1.lowerBound().doubleValue(), var2.lowerBound().doubleValue() };
            val upperBounds = new double[] { var1.upperBound().doubleValue(), var2.upperBound().doubleValue() };

            MultivariateFunction objective = point -> {
                args[objectiveVarIndex1] = point[0];
                args[objectiveVarIndex2] = point[1];
                val calc = newCalculation(args);
                return valueFunction.apply(calc).doubleValue();
            };

            val boundedFunction = new MultivariateFunctionMappingAdapter(objective, lowerBounds, upperBounds);
            val simpleBounds = new SimpleBounds(lowerBounds, upperBounds);
            val optimizer = new BOBYQAOptimizer(5);
            val result = optimizer.optimize(
                    maxEval,
                    new InitialGuess(new double[] { 0, 0 }),
                    new ObjectiveFunction(boundedFunction),
                    GoalType.MINIMIZE,
                    simpleBounds);

            val point = result.getPoint();

            return new Number[] { point[0], point[1] };
        }

        return new Number[] { -1, -1 };
    }

    @Override
    public Number[] maximize() {
        val args = getArgs();

        val objectiveVar1 = params[objectiveVarIndex1];
        val objectiveVar2 = params[objectiveVarIndex2];
        if (objectiveVar1 instanceof IndependentVar var1 && objectiveVar2 instanceof IndependentVar var2) {
            val lowerBounds = new double[] { var1.lowerBound().doubleValue(), var2.lowerBound().doubleValue() };
            val upperBounds = new double[] { var1.upperBound().doubleValue(), var2.upperBound().doubleValue() };

            MultivariateFunction objective = point -> {
                args[objectiveVarIndex1] = point[0];
                args[objectiveVarIndex2] = point[1];
                val calc = newCalculation(args);
                return valueFunction.apply(calc).doubleValue();
            };

            val boundedFunction = new MultivariateFunctionMappingAdapter(objective, lowerBounds, upperBounds);
            val simpleBounds = new SimpleBounds(lowerBounds, upperBounds);
            val optimizer = new BOBYQAOptimizer(5);
            val result = optimizer.optimize(
                    maxEval,
                    new InitialGuess(new double[] { 0, 0 }),
                    new ObjectiveFunction(boundedFunction),
                    GoalType.MAXIMIZE,
                    simpleBounds);

            val point = result.getPoint();

            return new Number[] { point[0], point[1] };
        }

        return new Number[] { -1, -1 };
    }

}
