package gg.mineral.bot.impl.math.optimization;

import java.util.function.Function;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;

import gg.mineral.bot.api.math.optimization.RecursiveCalculation;
import gg.mineral.bot.api.world.ClientWorld;
import lombok.val;

public class UnivariateOptimizer<C extends RecursiveCalculation<?>> extends Optimizer<C, Number>
        implements gg.mineral.bot.api.math.optimization.UnivariateOptimizer<C> {
    private final int objectiveVarIndex;

    public UnivariateOptimizer(ClientWorld world, Class<C> clazz, Function<C, Number> valueFunction, int maxEval,
            Param<?>[] params) {
        super(world, clazz, valueFunction, maxEval, params);

        var objectiveVarIndex = -1;

        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof IndependentVar) {
                objectiveVarIndex = i;
                break;
            }
        }

        this.objectiveVarIndex = objectiveVarIndex;
    }

    @Override
    public Number minimize() {
        val args = getArgs();

        val objectiveVar = params[objectiveVarIndex];
        if (objectiveVar instanceof IndependentVar var) {
            val searchInterval = new SearchInterval(var.lowerBound().doubleValue(), var.upperBound().doubleValue());
            UnivariateFunction objective = x -> {
                args[objectiveVarIndex] = x;
                val calc = newCalculation(args);
                return valueFunction.apply(calc).doubleValue();
            };
            val optimizer = new BrentOptimizer(1e-10, 1e-14);
            val result = optimizer.optimize(
                    maxEval,
                    new UnivariateObjectiveFunction(objective),
                    GoalType.MINIMIZE,
                    searchInterval);

            return result.getPoint();
        }

        return -1;
    }

    @Override
    public Number maximize() {
        val args = getArgs();

        val objectiveVar = params[objectiveVarIndex];
        if (objectiveVar instanceof IndependentVar var) {
            val searchInterval = new SearchInterval(var.lowerBound().doubleValue(), var.upperBound().doubleValue());
            UnivariateFunction objective = x -> {
                args[objectiveVarIndex] = x;
                val calc = newCalculation(args);
                return valueFunction.apply(calc).doubleValue();
            };
            val optimizer = new BrentOptimizer(1e-10, 1e-14);
            val result = optimizer.optimize(
                    maxEval,
                    new UnivariateObjectiveFunction(objective),
                    GoalType.MAXIMIZE,
                    searchInterval);

            return result.getPoint();
        }

        return -1;
    }

}
