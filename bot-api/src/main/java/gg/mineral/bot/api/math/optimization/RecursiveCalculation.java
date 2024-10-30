package gg.mineral.bot.api.math.optimization;

public interface RecursiveCalculation<R> {
    /**
     * Initializes the calculation.
     * 
     * @param args the arguments
     * 
     * @return the calculation
     */
    RecursiveCalculation<R> initialize(Object... args);

    /**
     * Computes the calculation.
     * 
     * @param maxEval the maximum number of evaluations
     * 
     * @return the result of the computation
     */
    R compute(int maxEval);
}
