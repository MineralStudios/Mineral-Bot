package gg.mineral.bot.api.math.optimization;

public interface RecursiveCalculation {
    enum Result {
        VALID, INVALID, CONTINUE;
    }

    /**
     * Initializes the calculation.
     *
     * @param args the arguments
     * @return the calculation
     */
    RecursiveCalculation initialize(Object... args);

    /**
     * Computes the calculation.
     *
     * @param maxEval the maximum number of evaluations
     * @return the result of the computation
     */
    Result compute(int maxEval);
}
