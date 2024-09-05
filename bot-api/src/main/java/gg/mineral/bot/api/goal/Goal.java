package gg.mineral.bot.api.goal;

import org.eclipse.jdt.annotation.NonNull;

import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Goal {
    @NonNull
    protected final FakePlayer fakePlayer;

    private long lastFixedTick = 0;

    public abstract boolean shouldExecute();

    public abstract void onTick();

    public abstract boolean onEvent(Event event);

    public abstract void onGameLoop();
}
