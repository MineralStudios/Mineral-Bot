package gg.mineral.bot.impl.math.optimization;

import gg.mineral.bot.api.math.optimization.RecursiveCalculation;
import lombok.val;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class UnivariateOptimizer<C extends RecursiveCalculation> extends Optimizer<C, Number>
        implements gg.mineral.bot.api.math.optimization.UnivariateOptimizer<C> {
    private final int objectiveVarIndex;

    public UnivariateOptimizer(Callable<C> callable, Function<C, Number> valueFunction, int maxEval,
                               Param<?>[] params) {
        super(callable, valueFunction, maxEval, params);

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
        if (objectiveVar instanceof IndependentVar(Number lowerBound, Number upperBound)) {
            val searchInterval = new SearchInterval(lowerBound.doubleValue(), upperBound.doubleValue());
            UnivariateFunction objective = x -> {
                args[objectiveVarIndex] = x;
                val calc = newCalculation(args);
                if (calc.compute(maxEval.getMaxEval()) == RecursiveCalculation.Result.VALID)
                    return valueFunction.apply(calc).doubleValue();
                else return Double.MAX_VALUE;
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
        if (objectiveVar instanceof IndependentVar(Number lowerBound, Number upperBound)) {
            val searchInterval = new SearchInterval(lowerBound.doubleValue(), upperBound.doubleValue());
            UnivariateFunction objective = x -> {
                args[objectiveVarIndex] = x;
                val calc = newCalculation(args);
                if (calc.compute(maxEval.getMaxEval()) == RecursiveCalculation.Result.VALID)
                    return valueFunction.apply(calc).doubleValue();
                else return Double.MIN_VALUE;
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
