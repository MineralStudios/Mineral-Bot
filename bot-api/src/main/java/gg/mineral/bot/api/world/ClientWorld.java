package gg.mineral.bot.api.world;

import java.util.Collection;

import gg.mineral.bot.api.entity.ClientEntity;
import gg.mineral.bot.api.math.optimization.RecursiveCalculation;
import gg.mineral.bot.api.math.optimization.Optimizer;
import gg.mineral.bot.api.world.block.Block;
import java.util.function.Function;

public interface ClientWorld {

    /**
     * Gets all entities in the world.
     * 
     * @return all entities in the world
     */
    Collection<ClientEntity> getEntities();

    /**
     * Gets the entity with the specified ID.
     * 
     * @param entityId
     *                 the entity ID
     * @return the entity with the specified ID
     */
    ClientEntity getEntityByID(int entityId);

    /**
     * Gets the block at the specified coordinates.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @param z
     *          the z-coordinate
     * @return the block at the specified coordinates
     */
    Block getBlockAt(int x, int y, int z);

    /**
     * Gets the block at the specified coordinates.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @param z
     *          the z-coordinate
     * @return the block at the specified coordinates
     */
    Block getBlockAt(double x, double y, double z);

    /**
     * Creates a new univariate optimizer.
     * 
     * @param calcClass
     *                      the calculation class
     * @param valueFunction
     *                      the value function (obtains the value to optimize)
     * @param maxEval
     *                      the maximum number of evaluations
     * 
     * @return the optimizer data
     */
    <C extends RecursiveCalculation<?>> Optimizer.Data<C, Number> univariateOptimizer(Class<C> calcClass,
            Function<C, Number> valueFunction,
            int maxEval);

    /**
     * Creates a new bivariate optimizer.
     * 
     * @param calcClass
     *                      the calculation class
     * @param valueFunction
     *                      the value function (obtains the value to optimize)
     * @param maxEval
     *                      the maximum number of evaluations
     * 
     * @return the optimizer data
     */
    <C extends RecursiveCalculation<?>> Optimizer.Data<C, Number[]> bivariateOptimizer(Class<C> calcClass,
            Function<C, Number> valueFunction,
            int maxEval);
}
