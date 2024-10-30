package gg.mineral.bot.api.math.trajectory.throwable;

public interface EnderPearlTrajectory extends ThrowableEntityTrajectory {

    @Override
    default float offset() {
        return 0.0F;
    }

    @Override
    default float power() {
        return 1.5F;
    }

    @Override
    default float gravity() {
        return 0.03F;
    }
}
